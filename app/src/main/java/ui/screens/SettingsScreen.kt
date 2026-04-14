package ui.screens

import android.content.ClipData
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.progettomobile.composable.BottomAppBar
import com.example.progettomobile.composable.TopAppBar

@Composable
fun SettingsScreen(navController : NavHostController){
    Scaffold(
        topBar = { TopAppBar("Settings", navController) },
        bottomBar = { BottomAppBar(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            Text("You are in the settings")
        }

    }
}