package com.example.progettomobile.composable

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import androidx.savedstate.SavedState
import it.supabase.remembermy.ui.screens.Home.HomeScreen
import it.supabase.remembermy.ui.screens.Settings.SettingsScreen
import it.supabase.remembermy.ui.screens.Settings.Theme
import it.supabase.remembermy.ui.screens.auth.AccessViewModel
import it.supabase.remembermy.ui.screens.auth.register.RegisterScreen
import it.supabase.remembermy.ui.screens.profile.ProfileViewModel
import it.supabase.remembermy.ui.screens.profile.ToProfile
import it.supabase.remembermy.ui.theme.ProgettoMobileTheme
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import it.supabase.remembermy.data.supabase.Events
import it.supabase.remembermy.ui.screens.Event.ChangeEventScreen
import it.supabase.remembermy.ui.screens.Event.CreateEventScreen
import it.supabase.remembermy.ui.screens.Event.EventPostScreen
import it.supabase.remembermy.ui.screens.Home.HomeViewModel
import it.supabase.remembermy.ui.screens.Map.MapScreen
import it.supabase.remembermy.ui.screens.auth.login.LoginScreen
import it.supabase.remembermy.ui.screens.auth.magiclogin.MagicLinkScreen
import it.supabase.remembermy.ui.screens.auth.recovery.RecoveryScreen
import it.supabase.remembermy.ui.screens.auth.reset.ResetPasswordScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import it.supabase.remembermy.ui.screens.camera.CameraScreen
import it.supabase.remembermy.ui.screens.profile.ChangeInfoProfileScreen
import it.supabase.remembermy.ui.screens.Map.MapViewModel
import it.supabase.remembermy.ui.screens.Search.SearchScreen
import it.supabase.remembermy.ui.screens.Search.SearchViewModel
import it.supabase.remembermy.ui.screens.camera.CameraViewModel
import it.supabase.remembermy.ui.theme.AppPalette
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

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
    data object Search : NavigationRoute

    @Serializable
    data class Profile(
        val idUser : String
    ) : NavigationRoute

    @Serializable
    data object Camera : NavigationRoute {
    }

    @Serializable
    data object ResetPassword : NavigationRoute

    @Serializable
    data object MagicLink : NavigationRoute

    @Serializable
    data class Map(
        val latitude: Double,
        val longitude: Double,
        val imagePic: String
    ) : NavigationRoute {
    }

    @Serializable
    data object ChangeInfo : NavigationRoute

    @Serializable
    data object CreateEvent : NavigationRoute

    @Serializable
    data class EventPost(
        val idEvent: String
    ) : NavigationRoute

    @Serializable
    data class ChangeEvent(
        val event : Events
    ) : NavigationRoute
}


@Composable
fun NavGraph(navController: NavHostController, supabase: SupabaseClient, start: NavigationRoute) {
    val accessModel = koinViewModel<AccessViewModel>()
    val sessionStatus by supabase.auth.sessionStatus.collectAsState()
    val cameraModel = koinViewModel<CameraViewModel>()
    val SearchModel = koinViewModel<SearchViewModel>()

    var selectedTheme by rememberSaveable { mutableStateOf(Theme.System) }
    var dynamicColor by rememberSaveable { mutableStateOf(true) }
    var selectedPalette by rememberSaveable { mutableStateOf(AppPalette.Blue) }

    LaunchedEffect(sessionStatus) {
        val currentDestination = navController.currentDestination
        val isHandlingDeepLink =
            currentDestination?.hasRoute<NavigationRoute.ResetPassword>() == true
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

                else -> {
                    navController.navigate(NavigationRoute.Login) {
                        popUpTo(0)
                    }
                }
            }
        }
    }
    ProgettoMobileTheme(
        darkTheme = when (selectedTheme) {
            Theme.Light -> false
            Theme.Dark -> true
            Theme.System -> isSystemInDarkTheme()
        },
        dynamicColor = dynamicColor,
        palette = selectedPalette
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val profileModel = koinViewModel<ProfileViewModel>()
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

                composable<NavigationRoute.HomeScreen> {
                    val homeModel = koinViewModel<HomeViewModel>()
                    HomeScreen(navController, homeModel)
                }
                composable<NavigationRoute.Settings> {
                    SettingsScreen(
                        navController,
                        selectedTheme = selectedTheme,
                        onThemeChange = { selectedTheme = it },
                        dynamicColor = dynamicColor,
                        onDynamicColorChange = { dynamicColor = it },
                        selectedPalette = selectedPalette,
                        onPaletteChange = {selectedPalette = it}
                    )
                }
                composable<NavigationRoute.Camera> {
                    CameraScreen(navController, cameraModel)
                }

                composable<NavigationRoute.Search> {
                    SearchScreen(navController, SearchModel)
                }
                composable<NavigationRoute.Profile> {backstackEntry ->
                    val entry = backstackEntry.toRoute<NavigationRoute.Profile>()
                    ToProfile(navController, profileModel, entry.idUser)
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
                composable<NavigationRoute.Map> { backStackEntry ->
                    val mapModel = koinViewModel<MapViewModel>()
                    val coordinates = backStackEntry.toRoute<NavigationRoute.Map>()
                    MapScreen(
                        navController = navController,
                        viewModel = mapModel,
                        latitude = coordinates.latitude,
                        longitude = coordinates.longitude,
                        imagePic = coordinates.imagePic
                    )
                }
                composable<NavigationRoute.CreateEvent> {
                    CreateEventScreen(
                        navController = navController,
                        vm = cameraModel
                    )
                }
                composable<NavigationRoute.EventPost> { backStackEntry ->
                    val route = backStackEntry.toRoute<NavigationRoute.EventPost>()
                    EventPostScreen(navController, SearchModel, route.idEvent)
                }

                composable<NavigationRoute.ChangeInfo> {
                    ChangeInfoProfileScreen(navController, profileModel)
                }
                composable<NavigationRoute.ChangeEvent>(
                    typeMap = mapOf(
                        typeOf<Events>() to parcelableType<Events>()
                    )
                ) {backStackEntry ->
                    val route = backStackEntry.toRoute<NavigationRoute.ChangeEvent>()
                    ChangeEventScreen(navController,profileModel, route.event)
                }
            }
        }
    }
}

inline fun <reified T : Parcelable> parcelableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {

    override fun put(bundle: SavedState, key: String, value: T)  = bundle.putParcelable(key, value)

    override fun get(bundle: Bundle, key: String) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelable(key)
        }

    override fun parseValue(value: String): T = json.decodeFromString(value)

    override fun serializeAsValue(value: T): String = Uri.encode(json.encodeToString(value))

}