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
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import it.supabase.remembermy.ui.screens.camera.CameraViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import it.supabase.remembermy.composable.TopAppBar
import com.example.progettomobile.composable.BottomAppBar
import com.example.progettomobile.composable.NavigationRoute
import it.supabase.remembermy.composable.ImagePickerButton
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
) {
    var name by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }
    var locationFound = remember { mutableStateOf(false) }
    var tag by remember { mutableStateOf("") }
    val suggestedTags = listOf(
        "Sport",
        "Cinema",
        "Musica",
        "Arte",
        "Libri",
        "Cibo",
        "Fiera",
        "Teatro",
        "Viaggi"
    )
    var pictureUri = vm.pictureUri
    var pictureSelected by remember {mutableStateOf(pictureUri != null)}
    var osmPlace by remember { mutableStateOf(false) }

    val gpsState = rememberGPSState()
    val osmState = rememberOSM()
    var waitingForLocation by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar("Creazione Evento", navController) },
        bottomBar = { BottomAppBar(navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(
                        rememberScrollState()
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Crea evento")
                AnimatedVisibility(visible = pictureSelected) {
                    Image(
                        painter = rememberAsyncImagePainter(pictureUri),
                        contentDescription = "Event Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                ImagePickerButton({
                    vm.setPictureData(it,
                        null)
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
                    value = place,
                    onValueChange = {
                        place = it
                        osmState.query.value = place
                        osmPlace = false
                    },
                    label = { Text("place") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                osmPlace = true
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
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    )
                )
                LocationDisabledAlert(
                    show = gpsState.showLocationDisabledAlert,
                    onAction = { gpsState.openLocationSettings() },
                    onHide = { gpsState.showLocationDisabledAlert = false }
                )



                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome evento") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    )
                )

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Dettagli evento") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    )
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Data evento") },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    )
                )

                OutlinedTextField(
                    value = tag,
                    onValueChange = {tag = it},
                    label = {Text("tag")},
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    )
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Row(

                    ) {
                            Text("Tag suggeriti.")
                    }

                    suggestedTags.forEach { suggestedTag ->
                        FilterChip(
                            selected = tag == suggestedTag,
                            onClick = { tag = suggestedTag },
                            label = {Text(suggestedTag)}
                        )
                    }
                }

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
                            if(name.isEmpty() || details.isEmpty() || date.isEmpty() || place.isEmpty()){
                                snackbarHostState.showSnackbar(
                                    message = "Nome, dettagli, data e luogo non possono essere vuoti",
                                    duration = SnackbarDuration.Short
                                )
                                return@launch
                            }
                            if(osmPlace){
                                osmState.latitudeResult.value =
                                    gpsState.coordinates.value?.latitude!!
                                osmState.longitudeResult.value =
                                    gpsState.coordinates.value?.longitude!!
                                locationFound.value = true
                            } else {
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
                            vm.createEvent(
                                name = name,
                                isPrivate = isPrivate,
                                date = date,
                                details = details,
                                coordinates = Coordinates(
                                    osmState.latitudeResult.value,
                                    osmState.longitudeResult.value
                                ),
                                placeName = place,
                                tags = tag
                            )
                                navController.navigate(
                                    NavigationRoute.Map(
                                        osmState.latitudeResult.value,
                                        osmState.longitudeResult.value,
                                        vm.finalUri ?: ""
                                    )
                                ) {
                                    popUpTo(NavigationRoute.HomeScreen)
                                }

                        }

                    },
                    onDismiss = { place = osmState.query.value },
                    onHide = { locationFound.value = false })
            }
        }
    }
}

@Composable
fun PlaceConfirmationAlert(
    show: Boolean,
    place: String,
    onAction: () -> Unit,
    onDismiss: () -> Unit,
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

