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
import androidx.navigation.NavHostController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomAppBar(navController: NavHostController){
    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = { navController.navigate(NavigationRoute.HomeScreen) }) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home"
                    )
                }
                IconButton(onClick = { navController.navigate(NavigationRoute.Search) }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
                IconButton(onClick = { navController.navigate(NavigationRoute.Profile) }){
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Account"
                    )
                }
                IconButton(onClick = {navController.navigate(NavigationRoute.Camera)}) {
                    Icon(
                        imageVector = Icons.Default.CameraEnhance,
                        contentDescription = "Camera"
                    )
                }
                IconButton(onClick = {navController.navigate(NavigationRoute.Map)}) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Map"
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary
    )
}