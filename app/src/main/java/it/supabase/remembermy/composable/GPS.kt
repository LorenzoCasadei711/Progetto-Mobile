package it.supabase.remembermy.composable


import android.Manifest
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.supabase.remembermy.data.Coordinates
import it.supabase.remembermy.data.LocationService
import it.supabase.remembermy.utils.MultiplePermissionHandler
import it.supabase.remembermy.utils.PermissionStatus
import it.supabase.remembermy.utils.rememberMultiplePermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class GPSState(
    private val ctx: Context,
    private val scope: CoroutineScope,
    val locationService: LocationService,
    val coordinates: State<Coordinates?>,
    val isLoading: State<Boolean>
) {
    var showLocationDisabledAlert by mutableStateOf(false)
    var showPermissionDeniedAlert by mutableStateOf(false)
    var showPermissionPermanentlyDeniedSnackBar by mutableStateOf(false)

    lateinit var locationPermissions: MultiplePermissionHandler

    fun getCurrentLocation() = scope.launch {
        try {
            locationService.getCurrentLocation()
        } catch (_: IllegalStateException) {
            showLocationDisabledAlert = true
        }
    }

    fun getLocationOrRequestPermission() {
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

@Composable
fun rememberGPSState(): GPSState {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationService = remember { LocationService(ctx) }
    val coordinates = locationService.coordinates.collectAsStateWithLifecycle()
    val isLoading = locationService.isLoading.collectAsStateWithLifecycle()


    val gpsState = remember(ctx, scope, locationService) {
        GPSState(ctx,
            scope,
            locationService,
            coordinates,
            isLoading)
    }

    val locationPermissions = rememberMultiplePermissions(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    ) { statuses ->
        when {
            statuses.any { it.value == PermissionStatus.Granted } -> gpsState.getCurrentLocation()
            statuses.all { it.value == PermissionStatus.PermanentlyDenied } ->
                gpsState.showPermissionPermanentlyDeniedSnackBar = true
            else ->
                gpsState.showPermissionDeniedAlert = true
        }
    }

    gpsState.locationPermissions = locationPermissions

    return gpsState
}
