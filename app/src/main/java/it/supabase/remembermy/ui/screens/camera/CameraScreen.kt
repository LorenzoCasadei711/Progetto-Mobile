package it.supabase.remembermy.ui.screens.camera

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.progettomobile.composable.NavigationRoute
import com.example.progettomobile.data.Coordinates
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import it.supabase.remembermy.utils.rememberMultiplePermissions

@Composable
fun CameraScreen(
    navController: NavHostController,
    vm : CameraViewModel = koinViewModel()
){
    val context = LocalContext.current
    val locationService = remember { LocationService(context) }
    val scope = rememberCoroutineScope()
    val locationPermissions = rememberMultiplePermissions(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        onResult = {}
    )
    val (_,takePicture) = rememberCameraLauncher(
        onPictureTaken = { uri ->
            scope.launch {

                val coordinates = try {
                    locationService.getCurrentLocation()
                } catch (e: Exception) {
                    null
                }
                /*val coordinates = Coordinates(
                    latitude = 44.1391,
                    longitude = 12.2431
                )*/
                println("GPS -> ${coordinates?.latitude}, ${coordinates?.longitude}")
                vm.setPictureData(
                    uri = uri,
                    coordinates = coordinates
                )
                navController.navigate(NavigationRoute.CreateEvent)
            }
        }
    )
    LaunchedEffect(locationPermissions.statuses) {
        val granted = locationPermissions.statuses.values.any { it.isGranted }

        if (granted) {
            takePicture()
        } else {
            locationPermissions.launcherPermissionRequest()
        }
    }

}