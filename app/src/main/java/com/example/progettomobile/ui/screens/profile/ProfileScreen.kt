package com.example.progettomobile.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import io.github.jan.supabase.SupabaseClient
import androidx.compose.runtime.collectAsState
import com.example.progettomobile.composable.TopAppBar
import com.example.progettomobile.data.supabase.Profiles

@Composable
fun ToProfile(navController : NavHostController, profileModel : ProfileViewModel){
    val user : Profiles? = profileModel.state.collectAsState().value.info
    Scaffold(
        topBar = { TopAppBar("Profile", navController) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding)
        ) {
            Button(
                onClick = {
                    profileModel.actions.logout()
                }
            ) {
                Text("Logout")
            }
        }
    }
}