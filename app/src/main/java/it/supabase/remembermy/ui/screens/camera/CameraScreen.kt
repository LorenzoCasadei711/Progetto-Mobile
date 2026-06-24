package it.supabase.remembermy.ui.screens.camera

//import android.app.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import com.example.progettomobile.composable.rememberCameraLauncher
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import it.supabase.remembermy.data.LocationService
import androidx.compose.runtime.rememberCoroutineScope
import com.example.progettomobile.composable.NavigationRoute
import kotlinx.coroutines.launch
import it.supabase.remembermy.utils.rememberMultiplePermissions
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import it.supabase.remembermy.utils.PermissionStatus
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.navigation.compose.NavHost
import com.example.progettomobile.composable.NavGraph

@Composable
fun CameraScreen(
    navController: NavHostController,
    vm : CameraViewModel
){
    val context = LocalContext.current
    val locationService = remember { LocationService(context) }
    val scope = rememberCoroutineScope()
    var showPermissionDialog by remember { mutableStateOf(false) }
    val permissions = rememberMultiplePermissions(
        permissions = listOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        onResult = {}
    )
    val (_,takePicture) = rememberCameraLauncher(
        onPictureTaken = { uri ->
            scope.launch {
                val locationGranted =
                    permissions.statuses[android.Manifest.permission.ACCESS_FINE_LOCATION]?.isGranted == true ||
                            permissions.statuses[android.Manifest.permission.ACCESS_COARSE_LOCATION]?.isGranted == true
                val coordinates =
                    if(locationGranted){
                        try {
                            locationService.getCurrentLocation()
                        } catch (e: Exception) {
                            null
                        }
                    }else{
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
    LaunchedEffect(permissions.statuses) {
        val granted = permissions.statuses[android.Manifest.permission.CAMERA]
        when(granted) {
            PermissionStatus.Granted -> {
                takePicture()
            }
            PermissionStatus.Unknown -> {
                permissions.launcherPermissionRequest()
            }
            PermissionStatus.Denied, PermissionStatus.PermanentlyDenied ->{
                showPermissionDialog = true
            }
            null -> {}
        }
    }
    if(showPermissionDialog){
        AlertDialog(
            onDismissRequest = {
                showPermissionDialog = false
            },
            title = {
                Text("Permesso fotocamera necessario")
            },
            text = {
                Text("Per scattare una foto devi consentire l'accesso alla fotocamera")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        permissions.launcherPermissionRequest()
                    }
                ) {
                    Text("Vai alle impostazioni")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Annulla")
                }
            }
        )
    }


}

