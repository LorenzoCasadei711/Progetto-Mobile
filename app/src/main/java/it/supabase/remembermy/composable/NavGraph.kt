package com.example.progettomobile.composable

import android.content.Intent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
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
import it.supabase.remembermy.ui.screens.auth.login.LoginScreen
import it.supabase.remembermy.ui.screens.auth.recovery.RecoveryScreen
import it.supabase.remembermy.ui.screens.auth.reset.ResetPasswordScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import it.supabase.remembermy.ui.screens.camera.CameraScreen

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
    data object Camera : NavigationRoute{
    }
    @Serializable
    data object ResetPassword : NavigationRoute
}

@Composable
fun NavGraph(navController : NavHostController, supabase : SupabaseClient, start : NavigationRoute){
    val accessModel = koinViewModel<AccessViewModel>()
    val sessionStatus by supabase.auth.sessionStatus.collectAsState()
    LaunchedEffect(sessionStatus) {
        when(sessionStatus){
        is SessionStatus.NotAuthenticated->{
            val currentRoute = navController.currentDestination?.route
            val isResetting = currentRoute?.contains("ResetPassword") == true
            if (!isResetting) {
                navController.navigate(NavigationRoute.Login) {
                    popUpTo(0)
                }
            }
        }
        is SessionStatus.Authenticated -> {
        if (navController.currentDestination?.hasRoute<NavigationRoute.Login>() == true ||
            navController.currentDestination?.hasRoute<NavigationRoute.Register>() == true) {
            navController.navigate(NavigationRoute.HomeScreen) {
                popUpTo(NavigationRoute.Login) { inclusive = true }
            }
        }
    }
        else -> {}
    }
    }

    NavHost(
        navController = navController,
        startDestination = start
    ){

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
            var selectedTheme by rememberSaveable { mutableStateOf(Theme.System)}
            var dynamicColor by rememberSaveable {mutableStateOf(true)}
            ProgettoMobileTheme (
                darkTheme = when(selectedTheme){
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.System -> isSystemInDarkTheme()
                },
                dynamicColor = dynamicColor
            ){
                SettingsScreen(
                    navController,
                    selectedTheme = selectedTheme,
                    onThemeChange = {selectedTheme = it},
                    dynamicColor = dynamicColor,
                    onDynamicColorChange = {dynamicColor = it}
                )
            }
        }
        composable<NavigationRoute.Camera> { CameraScreen(navController) }
        //composable<NavigationRoute.Add> {  }
        //composable<NavigationRoute.Search> {  }
        composable<NavigationRoute.Profile> {
            val profileModel = koinViewModel<ProfileViewModel>()
            ToProfile(navController, profileModel)
        }

        composable<NavigationRoute.ResetPassword>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "it.supabase.remembermy://reset-password.*"
                }
            )
        ){backStackEntry->
            val intentData = backStackEntry.arguments?.getParcelable<Intent>(
                NavController.KEY_DEEP_LINK_INTENT)
            val fullUri = intentData?.data?.getQueryParameter("code")
            println(fullUri)
            ResetPasswordScreen(accessModel, navController)
        }
    }
}