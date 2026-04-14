package com.example.progettomobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import ui.screens.HomeScreen
import ui.screens.SettingsScreen

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

}

@Composable
fun NavGraph(navController : NavHostController){
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.HomeScreen
    ){
        //composable<NavigationRoute.Login> {  }
        composable<NavigationRoute.HomeScreen> { HomeScreen(navController) }
        composable<NavigationRoute.Settings> { SettingsScreen(navController) }
        //composable<NavigationRoute.Add> {  }
        //composable<NavigationRoute.Search> {  }
        //composable<NavigationRoute.Profile> {  }
    }
}