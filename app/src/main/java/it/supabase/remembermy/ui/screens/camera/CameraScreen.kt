package it.supabase.remembermy.ui.screens.camera

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.progettomobile.composable.BottomAppBar
import it.supabase.remembermy.composable.TopAppBar
import com.example.progettomobile.composable.rememberCameraLauncher
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import com.example.progettomobile.data.LocationService
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CameraScreen(
    navController: NavHostController,
    vm : CameraViewModel = viewModel()
){
    val context = LocalContext.current
    val locationService = remember { LocationService(context) }
    val scope = rememberCoroutineScope()
    val (pictureUri,takePicture) = rememberCameraLauncher(
        onPictureTaken = { uri ->
            scope.launch {
                val coordinates = try {
                    locationService.getCurrentLocation()
                } catch (e: Exception) {
                    null
                }
                vm.onPictureTaken(
                    uri = uri,
                    coordinates = coordinates
                )
            }
        }
    )
    Scaffold(
        bottomBar = { BottomAppBar(navController) },
        topBar = { TopAppBar("App",navController) }
    ) {paddingValues ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(paddingValues)
        ) {
            Button(onClick = takePicture) {
                Text("Scatta foto")
            }

            vm.pictureUri?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "foto scattata"
                )
            }
        }
    }




}