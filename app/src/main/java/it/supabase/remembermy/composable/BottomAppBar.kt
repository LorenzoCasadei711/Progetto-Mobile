package com.example.progettomobile.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import com.example.progettomobile.composable.NavigationRoute


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomAppBar(navController: NavHostController) {
    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = { navController.navigateSingleTop(NavigationRoute.HomeScreen) },
                    enabled = navController.currentDestination?.hasRoute<NavigationRoute.HomeScreen>() == false
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home"
                    )
                }
                IconButton(
                    onClick = { navController.navigateSingleTop(NavigationRoute.Search) },
                    enabled = navController.currentDestination?.hasRoute<NavigationRoute.Search>() == false
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
                IconButton(
                    onClick = { navController.navigateSingleTop(NavigationRoute.Profile) },
                    enabled = navController.currentDestination?.hasRoute<NavigationRoute.Profile>() == false
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Account"
                    )
                }
                IconButton(
                    onClick = { navController.navigateSingleTop(NavigationRoute.Camera) },
                    enabled = navController.currentDestination?.hasRoute<NavigationRoute.Camera>() == false
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraEnhance,
                        contentDescription = "Camera"
                    )
                }
                IconButton(
                    onClick = {
                        navController.navigateSingleTop(
                            NavigationRoute.Map(
                                latitude = 44.1391,
                                longitude = 12.2431,
                                imagePic = ""
                            )
                        )
                    },
                    enabled = navController.currentDestination?.hasRoute<NavigationRoute.Map>() == false
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Map"
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    )
}

private fun NavHostController.navigateSingleTop(route: NavigationRoute) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
        if(route == NavigationRoute.HomeScreen){
            popUpTo(0)
        }
    }
}