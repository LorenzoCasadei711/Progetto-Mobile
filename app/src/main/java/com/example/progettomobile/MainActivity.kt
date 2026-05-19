package com.example.progettomobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.progettomobile.composable.rememberCameraLauncher
import kotlinx.serialization.Serializable
import it.supabase.remembermy.ui.screens.HomeScreen
import it.supabase.remembermy.ui.screens.SettingsScreen
import it.supabase.remembermy.ui.screens.Theme
import it.supabase.remembermy.ui.theme.ProgettoMobileTheme
import it.supabase.remembermy.ui.screens.MapScreen
import it.supabase.remembermy.ui.screens.camera.CameraScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavGraph(navController)
        }
    }
}

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
    @Serializable
    data object Map : NavigationRoute
}

@Composable
fun NavGraph(navController : NavHostController){
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.HomeScreen
    ){
        //composable<NavigationRoute.Login> {  }
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
        composable<NavigationRoute.Map> { MapScreen(navController) }
        //composable<NavigationRoute.Add> {  }
        //composable<NavigationRoute.Search> {  }
        //composable<NavigationRoute.Profile> {  }
    }
}