package com.example.progettomobile.composable

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import it.supabase.remembermy.ui.screens.HomeScreen
import it.supabase.remembermy.ui.screens.SettingsScreen
import it.supabase.remembermy.ui.screens.Theme
import it.supabase.remembermy.ui.screens.auth.AccessViewModel
import it.supabase.remembermy.ui.screens.auth.register.RegisterScreen
import it.supabase.remembermy.ui.screens.profile.ProfileViewModel
import it.supabase.remembermy.ui.screens.profile.ToProfile
import it.supabase.remembermy.ui.theme.ProgettoMobileTheme
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import it.supabase.remembermy.ui.screens.Event.CreateEventScreen
import it.supabase.remembermy.ui.screens.Map.MapScreen
import it.supabase.remembermy.ui.screens.auth.login.LoginScreen
import it.supabase.remembermy.ui.screens.auth.magiclogin.MagicLinkScreen
import it.supabase.remembermy.ui.screens.auth.recovery.RecoveryScreen
import it.supabase.remembermy.ui.screens.auth.reset.ResetPasswordScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import it.supabase.remembermy.ui.screens.camera.CameraScreen
import it.supabase.remembermy.ui.screens.Map.MapViewModel
import it.supabase.remembermy.ui.screens.camera.CameraViewModel

sealed interface NavigationRoute {

    @Serializable
    data object Login : NavigationRoute

    @Serializable
    data object Register : NavigationRoute

    @Serializable
    data object Recovery : NavigationRoute

    @Serializable
    data object HomeScreen : NavigationRoute

    @Serializable
    data object Settings : NavigationRoute

    @Serializable
    data object Add : NavigationRoute

    @Serializable
    data object Search : NavigationRoute

    @Serializable
    data object Profile : NavigationRoute

    @Serializable
    data object Camera : NavigationRoute {
    }

    @Serializable
    data object ResetPassword : NavigationRoute
    @Serializable
    data object MagicLink : NavigationRoute
    @Serializable
    data object Map : NavigationRoute{
    }
    @Serializable
    data object CreateEvent : NavigationRoute
}

@Composable
fun NavGraph(navController: NavHostController, supabase: SupabaseClient, start: NavigationRoute) {

    val accessModel = koinViewModel<AccessViewModel>()
    val sessionStatus by supabase.auth.sessionStatus.collectAsState()
    val cameraModel = koinViewModel<CameraViewModel>()

    var selectedTheme by rememberSaveable { mutableStateOf(Theme.System) }
    var dynamicColor by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(sessionStatus) {
        val currentDestination = navController.currentDestination
        val isHandlingDeepLink = currentDestination?.hasRoute<NavigationRoute.ResetPassword>() == true
        if (!isHandlingDeepLink) {
            when (sessionStatus) {
                is SessionStatus.NotAuthenticated -> {
                    navController.navigate(NavigationRoute.Login) {
                        popUpTo(0)
                    }
                }

                is SessionStatus.Authenticated -> {
                    if (currentDestination?.hasRoute<NavigationRoute.Login>() == true ||
                        currentDestination?.hasRoute<NavigationRoute.Register>() == true
                    ) {
                        navController.navigate(NavigationRoute.HomeScreen) {
                            popUpTo(NavigationRoute.Login) { inclusive = true }
                        }
                    }
                }
                else -> {}
            }
        }
    }
    ProgettoMobileTheme(
        darkTheme = when (selectedTheme) {
            Theme.Light -> false
            Theme.Dark -> true
            Theme.System -> isSystemInDarkTheme()
        },
        dynamicColor = dynamicColor
    ){
        NavHost(
            navController = navController,
            startDestination = start
        ) {


            composable<NavigationRoute.Register> {
                RegisterScreen(accessModel, navController)
            }
            composable<NavigationRoute.Login> {
                LoginScreen(accessModel, navController)
            }

            composable<NavigationRoute.Recovery> {
                RecoveryScreen(accessModel, navController)
            }

            composable<NavigationRoute.HomeScreen> { HomeScreen(navController) }
            composable<NavigationRoute.Settings> {
                SettingsScreen(
                    navController,
                    selectedTheme = selectedTheme,
                    onThemeChange = { selectedTheme = it },
                    dynamicColor = dynamicColor,
                    onDynamicColorChange = { dynamicColor = it }
                )
            }
            composable<NavigationRoute.Camera> { CameraScreen(navController,cameraModel) }
            //composable<NavigationRoute.Add> {  }
            //composable<NavigationRoute.Search> {  }
            composable<NavigationRoute.Profile> {
                val profileModel = koinViewModel<ProfileViewModel>()
                ToProfile(navController, profileModel)
            }

            composable<NavigationRoute.ResetPassword>(
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "it.supabase.remembermy://login-callback*type=recovery*"
                    }
                )
            ) {
                ResetPasswordScreen(accessModel, navController)
            }

            composable<NavigationRoute.MagicLink> {
                MagicLinkScreen(accessModel, navController)
            }
            composable<NavigationRoute.Map> {
                val mapModel = koinViewModel<MapViewModel>()
                MapScreen(
                    navController = navController,
                    viewModel = mapModel
                )
            }
            composable<NavigationRoute.CreateEvent> {
                CreateEventScreen(
                    navController = navController,
                    vm = cameraModel
                )
            }
        }
    }

}