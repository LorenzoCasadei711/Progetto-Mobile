package it.supabase.remembermy.composable


import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import it.supabase.remembermy.data.Coordinates
import it.supabase.remembermy.data.LocationService
import it.supabase.remembermy.data.repository.OSMDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class OSMState(
    private val ctx : Context,
    private val scope : CoroutineScope,
    val locationService: LocationService,
    val osmDataSource: OSMDataSource,
    val snackbarHostState : SnackbarHostState
){
    var query : MutableState<String> = mutableStateOf("")
    var result : MutableState<String> = mutableStateOf("")

    var latitudeResult : MutableState<Double> = mutableDoubleStateOf(0.0)
    var longitudeResult : MutableState<Double> = mutableDoubleStateOf(0.0)

    var coordinates : MutableState<Coordinates> = mutableStateOf(Coordinates(0.0, 0.0))
    var errorOSM : MutableState<String> = mutableStateOf("")
    fun isOnline() : Boolean{
        val connectivityManager = ctx
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager
            .getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ==true ||
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }

    fun openWirelessSettings(){
        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if(intent.resolveActivity(ctx.packageManager) != null){
            ctx.startActivity(intent)
        }

    }

    fun searchPlaces() = scope.launch {
        if(isOnline()){
            result.value = "Loading..."
            Log.d("DEBUG", query.value)
            val res = osmDataSource.searchPlaces(query.value)
            result.value = res.getOrNull(0)?.displayName ?: "Place not found"
            latitudeResult.value = res.getOrNull(0)?.latitude ?: -1.0
            longitudeResult.value = res.getOrNull(0)?.longitude ?: -1.0
        } else {
            val res = snackbarHostState.showSnackbar(
                message = "No Internet connectivity",
                actionLabel = "Go to Settings",
                duration = SnackbarDuration.Long
            )
            if (res == SnackbarResult.ActionPerformed) {
                openWirelessSettings()
            }
        }
    }

    fun searchWithCoords() = scope.launch {
        if(isOnline()){
            errorOSM.value = ""
            result.value = "Loading..."
            val res = osmDataSource.searchWithCoordinates(coordinates.value.latitude, coordinates.value.longitude)
            if(res == null) {
                errorOSM.value = "Error during the search of the place"
            }else{
                result.value = res.displayName
            }
        } else {
            val res = snackbarHostState.showSnackbar(
                message = "No Internet connectivity",
                actionLabel = "Go to Settings",
                duration = SnackbarDuration.Long
            )
            if (res == SnackbarResult.ActionPerformed) {
                openWirelessSettings()
            }
        }
    }
}

@Composable
fun rememberOSM(): OSMState{
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationService = remember { LocationService(ctx) }
    val osmDataSource = koinInject<OSMDataSource>()
    val snackbarHostState = remember { SnackbarHostState() }

    val osmState = remember(ctx, scope, locationService, osmDataSource, snackbarHostState){
        OSMState(ctx, scope, locationService, osmDataSource, snackbarHostState)
    }
    var query by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }


    return osmState
}