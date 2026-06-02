package it.supabase.remembermy.ui.screens.Event

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import it.supabase.remembermy.ui.screens.camera.CameraViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.rotate
import it.supabase.remembermy.composable.TopAppBar
import com.example.progettomobile.composable.BottomAppBar
import com.example.progettomobile.composable.NavigationRoute
import it.supabase.remembermy.composable.rememberGPSState
import it.supabase.remembermy.composable.rememberOSM
import it.supabase.remembermy.data.Coordinates
import it.supabase.remembermy.ui.LocationDisabledAlert
import it.supabase.remembermy.utils.PermissionStatus
import kotlinx.coroutines.launch

@Composable
fun CreateEventScreen(
    navController: NavHostController,
    vm: CameraViewModel
){
    var name by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }
    var locationFound by remember { mutableStateOf(false) }

    val gpsState = rememberGPSState()
    val osmState = rememberOSM()
    var waitingForLocation by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }


    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar("Creazione Evento", navController) },
        bottomBar = {BottomAppBar(navController)},
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Crea evento")

            LaunchedEffect(gpsState.coordinates.value, waitingForLocation) {
                val currentCoords = gpsState.coordinates.value
                if (waitingForLocation && currentCoords != null) {
                    osmState.coordinates.value = currentCoords
                    osmState.searchWithCoords()
                    waitingForLocation = false
                }
            }

            LaunchedEffect(key1 = osmState.result.value) {
                val res = osmState.result.value
                if (res.isNotEmpty() && res != "Loading..." && res != "Place not found") {
                    place = res
                }
            }

            OutlinedTextField(
                value = place,
                onValueChange = {
                    place = it
                    osmState.query.value = place
                },
                label = { Text("place") },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            waitingForLocation = true
                            gpsState.getLocationOrRequestPermission()
                            if(!gpsState.locationPermissions.statuses.entries.all { it.value == PermissionStatus.Granted }) {
                                gpsState.showLocationDisabledAlert = true
                                waitingForLocation = false
                            }
                        }
                    ) {
                        val infiniteTransition =
                            rememberInfiniteTransition(label = "loading")
                        val rotation by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = -360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "rotation"
                        )
                        if (waitingForLocation) {
                            Icon(
                                imageVector = Icons.Filled.Replay,
                                contentDescription = "Loading",
                                modifier = Modifier.rotate(rotation) // Apply the animated rotation here
                            )
                        } else {
                            Icon(Icons.Filled.GpsFixed, "GPS Icon")
                        }
                    }
                }
            )
            LocationDisabledAlert(
                show = gpsState.showLocationDisabledAlert,
                onAction = { gpsState.openLocationSettings() },
                onHide = {gpsState.showLocationDisabledAlert = false
                        waitingForLocation = false}
            )
            PlaceConfirmationAlert(
                show = locationFound,
                place = osmState.result.value,
                onAction = {
                    scope.launch {
                        vm.createEvent(
                            name = name,
                            isPrivate = isPrivate,
                            date = date,
                            details = details,
                            coordinates = Coordinates(osmState.latitudeResult.value,
                                osmState.longitudeResult.value)
                        )
                        navController.navigate(NavigationRoute.Map){
                            popUpTo(NavigationRoute.HomeScreen)
                        }
                    }
                           },
                onDismiss = {place = osmState.query.value},
                onHide = {locationFound = false}
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome evento") }
            )

            OutlinedTextField(
                value = details,
                onValueChange = { details = it },
                label = { Text("Dettagli evento") }
            )

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Data evento") }
            )

            Row {
                Checkbox(
                    checked = isPrivate,
                    onCheckedChange = { isPrivate = it }
                )
                Text("Evento privato")
            }

            Button(
                onClick = {
                    scope.launch {
                        if (gpsState.coordinates.value == null){
                            osmState.searchPlaces().join()
                        }
                        if(!osmState.emptyResult.value){
                            locationFound = true
                        }else{
                            snackbarHostState.showSnackbar(
                                message = "Place Not Found",
                                duration = SnackbarDuration.Long
                            )
                        }
                    }
                }
            ) {
                Text("Crea il tuo evento")
            }
        }
    }
}

@Composable
fun PlaceConfirmationAlert(
    show: Boolean,
    place : String,
    onAction: () -> Unit,
    onDismiss: ()  -> Unit,
    onHide: () -> Unit
) {
    if (show) {
        AlertDialog(
            title = { Text("Confirm Location") },
            text = { Text("Is this your location? $place") },
            confirmButton = {
                TextButton(onClick = {
                    onAction()
                    onHide()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onHide()
                    onDismiss()
                }) {
                    Text("No")
                }
            },
            onDismissRequest = onHide
        )
    }
}