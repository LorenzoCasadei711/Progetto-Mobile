package it.supabase.remembermy.ui.screens.Map

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.osmdroid.views.overlay.Marker
import com.example.progettomobile.utils.PlaceInfoWindow
import it.supabase.remembermy.R
import com.example.progettomobile.data.LocationService
import it.supabase.remembermy.composable.TopAppBar
import androidx.navigation.NavHostController
import com.example.progettomobile.composable.BottomAppBar

@Composable
fun MapScreen(
    navController: NavHostController,
    viewModel: MapViewModel
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = { TopAppBar("Mappa", navController) },
        bottomBar = { BottomAppBar(navController) }
    )
    {paddingValues ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            factory = { context ->
                Configuration.getInstance().userAgentValue = context.packageName

                MapView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)

                    val cesena = GeoPoint(44.1391, 12.2431)

                    controller.setZoom(15.0)
                    controller.setCenter(cesena)
                }
            },
            update = { mapView ->
                mapView.overlays.clear()

                state.events.forEach { event ->
                    val marker = Marker(mapView).apply {
                        position = GeoPoint(event.latitude, event.longitude)
                        title = event.name_event

                        setAnchor(
                            Marker.ANCHOR_CENTER,
                            Marker.ANCHOR_BOTTOM
                        )

                        infoWindow = PlaceInfoWindow(
                            mapView = mapView,
                            imageResId = R.drawable.cesena_centro,
                            description = event.date_event
                        )
                        setOnMarkerClickListener { marker, mapView ->
                            if (marker.isInfoWindowShown) {
                                marker.closeInfoWindow()
                            } else {
                                marker.showInfoWindow()
                            }

                            true
                        }
                    }

                    mapView.overlays.add(marker)
                }

                mapView.invalidate()


            }
        )
    }
}
                /*val puntiInteresse = listOf(
                    GeoPoint(44.1391, 12.2431) to "Cesena centro",
                    GeoPoint(44.1464, 12.2362) to "Stazione di Cesena",
                    GeoPoint(44.1376, 12.2464) to "Biblioteca Malatestiana"
                )
                val mapView = this*/

                /*puntiInteresse.forEach { (posizione, nome) ->
                    val marker = Marker(this).apply {
                        position = posizione
                        title = nome
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        infoWindow = PlaceInfoWindow(
                            mapView = mapView,
                            imageResId = R.drawable.cesena_centro,
                            description = "Centro storico di Cesena"
                        )
                        setOnMarkerClickListener { marker, mapView ->
                            if (marker.isInfoWindowShown) {
                                marker.closeInfoWindow()
                            } else {
                                marker.showInfoWindow()
                            }

                            true
                        }
                    }

                    overlays.add(marker)
                }*/





