package it.supabase.remembermy.ui.screens.Event

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.progettomobile.composable.BottomAppBar
import com.example.progettomobile.composable.NavigationRoute
import it.supabase.remembermy.composable.ImagePickerButton
import it.supabase.remembermy.composable.TopAppBar
import it.supabase.remembermy.composable.rememberGPSState
import it.supabase.remembermy.composable.rememberOSM
import it.supabase.remembermy.data.supabase.Events
import it.supabase.remembermy.ui.LocationDisabledAlert
import it.supabase.remembermy.ui.screens.profile.ProfileViewModel
import it.supabase.remembermy.utils.PermissionStatus
import kotlinx.coroutines.launch

@Composable
fun ChangeEventScreen(navController : NavHostController, viewModel : ProfileViewModel,event : Events){
    if(event.id_user == viewModel.state.collectAsState().value.info?.id_user){

        var name by remember { mutableStateOf(event.name_event) }
        var details by remember { mutableStateOf(event.event_details) }
        var place by remember { mutableStateOf(event.place_name) }
        var date by remember { mutableStateOf(event.date_event) }
        var isPrivate by remember { mutableStateOf(event.is_private) }
        val locationFound = remember { mutableStateOf(false) }
        var pictureUri by remember { mutableStateOf(event.event_photo?.toUri()) }
        var pictureSelected by remember {mutableStateOf(pictureUri != null)}

        val gpsState = rememberGPSState()
        val osmState = rememberOSM()
        var waitingForLocation by remember { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }

        val scope = rememberCoroutineScope()

        Scaffold(
            topBar = { TopAppBar("Modifica Evento", navController) },
                    bottomBar = { BottomAppBar( navController) },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Crea evento")
                    AnimatedVisibility(visible = pictureSelected) {
                        Image(
                            painter = rememberAsyncImagePainter(pictureUri),
                            contentDescription = "Event Image",
                            modifier = Modifier.size(400.dp)
                        )
                    }





                    ImagePickerButton({
                        pictureUri = it
                        pictureSelected = true})
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
                        if (res.isNotEmpty() && res != "Loading..." && res != "Place not found" && !locationFound.value) {
                            place = res
                        }
                    }

                    OutlinedTextField(
                        value = place.orEmpty(),
                        onValueChange = {
                            place = it
                            osmState.query.value = place.orEmpty()
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
                                    if (!gpsState.locationPermissions.statuses.entries.all { it.value == PermissionStatus.Granted }) {
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
                                if (gpsState.isLoading.value) {
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
                        onHide = { gpsState.showLocationDisabledAlert = false }
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome evento") }
                    )

                    OutlinedTextField(
                        value = details.orEmpty(),
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
                                osmState.searchPlaces().join()
                                val result = osmState.result.value
                                if (result != "Place not found" && result != "Loading..." && result.isNotEmpty()) {
                                    locationFound.value = true
                                } else {
                                    snackbarHostState.showSnackbar(
                                        message = "Place not found",
                                        duration = SnackbarDuration.Long
                                    )
                                }
                            }
                        }
                    ) {
                        Text("Crea il tuo evento")
                    }

                }
                if (locationFound.value) {
                    PlaceConfirmationAlert(
                        show = locationFound.value,
                        place = osmState.result.value,
                        onAction = {
                            Log.d("DEBUG", "The location is $name")
                            scope.launch {
                                val newEvent = Events(
                                    id_event = event.id_event,
                                    status_event = event.status_event,
                                    name_event = name,
                                    is_private = isPrivate,
                                    date_event = date,
                                    id_user = event.id_user,
                                    event_photo = event.event_photo,
                                    latitude = osmState.latitudeResult.value,
                                    longitude = osmState.longitudeResult.value,
                                    place_name = osmState.result.value,
                                    event_details = details,
                                    followedEvents = emptyList(),
                                    opinions = emptyList()
                                )
                                viewModel.actions.editEvent(newEvent,pictureUri)
                                navController.navigate(NavigationRoute.Profile)
                            }

                        },
                        onDismiss = { place = osmState.query.value },
                        onHide = { locationFound.value = false })
                }
            }

        }
    }else{
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            TextButton(
                onClick = {
                    navController.navigate(NavigationRoute.HomeScreen){
                        popUpTo(0)
                    }
                }
            ) { Text("How did you get here?")}
        }
    }
}
