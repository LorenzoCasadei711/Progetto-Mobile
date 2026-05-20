package ui.screens
import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.progettomobile.data.LocationService
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.progettomobile.data.Coordinates
import it.supabase.remembermy.utils.rememberMultiplePermissions
import it.supabase.remembermy.utils.PermissionStatus
import android.Manifest.permission
import android.content.Intent
import androidx.compose.ui.text.font.FontVariation
import android.provider.Settings

@Composable
fun GPSScreen(){
    var showLocationDisabledAlert by remember { mutableStateOf(false) }
    var showPermissionDeniedAlert by remember { mutableStateOf(false) }
    var showPermissionPermanentlyDeniedSnackbar by remember { mutableStateOf(false) }

    val ctx = LocalContext.current
    val locationService = remember { LocationService(ctx) }

    val coordinates by locationService.coordinates.collectAsStateWithLifecycle()
    val isLoading by locationService.isLoading.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    fun getCurrentLocation() = scope.launch {
        try {
            locationService.getCurrentLocation()
        } catch (_: IllegalStateException) {
            showLocationDisabledAlert = true
        }
    }
    val locationPermissions = rememberMultiplePermissions(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    ) { statuses ->
        when {
            statuses.any { it.value == PermissionStatus.Granted } ->
                getCurrentLocation()
            statuses.all { it.value == PermissionStatus.PermanentlyDenied } ->
                showPermissionPermanentlyDeniedSnackbar = true
            else ->
                showPermissionDeniedAlert = true
        }
    }
    fun getLocationOrRequestPermission(){
        if (locationPermissions.statuses.any { it.value.isGranted }) {
            getCurrentLocation()
        } else {
            locationPermissions.launcherPermissionRequest()
        }
    }
    fun openLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (intent.resolveActivity(ctx.packageManager) != null) {
            ctx.startActivity(intent)
        }
    }
}
