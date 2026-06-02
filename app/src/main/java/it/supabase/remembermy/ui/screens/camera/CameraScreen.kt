package it.supabase.remembermy.ui.screens.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.example.progettomobile.composable.rememberCameraLauncher
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import it.supabase.remembermy.data.LocationService
import androidx.compose.runtime.rememberCoroutineScope
import com.example.progettomobile.composable.NavigationRoute
import kotlinx.coroutines.launch
import it.supabase.remembermy.utils.rememberMultiplePermissions

@Composable
fun CameraScreen(
    navController: NavHostController,
    vm : CameraViewModel
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