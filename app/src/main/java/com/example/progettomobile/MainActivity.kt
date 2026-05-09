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
import com.example.progettomobile.data.supabase.Supabase
import com.example.progettomobile.data.supabase.SupabaseAuth
import com.example.progettomobile.ui.screens.HomeScreen
import com.example.progettomobile.ui.screens.SettingsScreen
import com.example.progettomobile.ui.screens.Theme
import com.example.progettomobile.ui.screens.access.AccessViewModel
import com.example.progettomobile.ui.theme.ProgettoMobileTheme
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import ui.screens.camera.CameraScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val supabase = Supabase()
        super.onCreate(savedInstanceState)
        supabase.handleDeeplinks(intent)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavGraph(navController, supabase)
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
}

@Composable
fun NavGraph(navController : NavHostController, supabase : SupabaseClient){
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.HomeScreen
    ){
        composable<NavigationRoute.Login> {
            val viewModel = koinViewModel<AccessViewModel>()

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
        //composable<NavigationRoute.Profile> {  }
    }
}