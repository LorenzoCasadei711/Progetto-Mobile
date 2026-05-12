package com.example.progettomobile.composable

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.progettomobile.ui.screens.HomeScreen
import com.example.progettomobile.ui.screens.SettingsScreen
import com.example.progettomobile.ui.screens.Theme
import com.example.progettomobile.ui.screens.access.AccessViewModel
import com.example.progettomobile.ui.screens.access.ToAccessScreen
import com.example.progettomobile.ui.screens.profile.ProfileViewModel
import com.example.progettomobile.ui.screens.profile.ToProfile
import com.example.progettomobile.ui.theme.ProgettoMobileTheme
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import ui.screens.camera.CameraScreen

sealed interface NavigationRoute {

    @Serializable
    data object Login : NavigationRoute
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
}

@Composable
fun NavGraph(navController : NavHostController, supabase : SupabaseClient){
    val accessModel = koinViewModel<AccessViewModel>()
    val sessionStatus by supabase.auth.sessionStatus.collectAsState()
    LaunchedEffect(sessionStatus) {
        if(sessionStatus !is SessionStatus.Authenticated){
            navController.navigate(NavigationRoute.Login){
                popUpTo(0)
            }

        }
    }

    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Login
    ){

        composable<NavigationRoute.Login> {
            when (sessionStatus) {
                is SessionStatus.Authenticated -> {
                    HomeScreen(navController)
                }
                else -> {
                    ToAccessScreen(accessModel, navController)
                }
            }

        }
        composable<NavigationRoute.HomeScreen> { HomeScreen(navController) }
        composable<NavigationRoute.Settings> {
            var selectedTheme by rememberSaveable { mutableStateOf(Theme.System)}
            var dinamicColor by rememberSaveable {mutableStateOf(true)}
            ProgettoMobileTheme (
                darkTheme = when(selectedTheme){
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.System -> isSystemInDarkTheme()
                },
                dynamicColor = dinamicColor
            ){
                SettingsScreen(
                    navController,
                    selectedTheme = selectedTheme,
                    onThemeChange = {selectedTheme = it},
                    dynamicColor = dinamicColor,
                    onDynamicColorChange = {dinamicColor = it}
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
    }
}