package it.supabase.remembermy.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import com.example.progettomobile.composable.NavigationRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(title: String,navController: NavHostController){
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if(navController.previousBackStackEntry != null){
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Go Back")

                }

            }
        },
        actions = {

            IconButton(onClick = { navController.navigate(NavigationRoute.Settings) },
                enabled = navController.currentDestination?.hasRoute<NavigationRoute.Settings>() == false) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }

            if(title != "Login"){
                IconButton(onClick = { navController.navigate(NavigationRoute.CreateEvent) },
                    enabled = navController.currentDestination?.hasRoute<NavigationRoute.CreateEvent>() == false) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}