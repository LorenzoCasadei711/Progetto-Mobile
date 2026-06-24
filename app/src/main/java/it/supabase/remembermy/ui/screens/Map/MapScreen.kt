package it.supabase.remembermy.ui.screens.Map

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.osmdroid.views.overlay.Marker
import com.example.progettomobile.utils.PlaceInfoWindow
import it.supabase.remembermy.R
import it.supabase.remembermy.composable.TopAppBar
import androidx.navigation.NavHostController
import com.example.progettomobile.composable.BottomAppBar

@Composable
fun MapScreen(
    navController: NavHostController,
    viewModel: MapViewModel,
    latitude : Double = 44.1391,
    longitude : Double = 12.2431,
    imagePic : String = ""
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

                    val center = GeoPoint(latitude, longitude)

                    controller.setZoom(15.0)
                    controller.setCenter(center)
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
                            imageUrl = event.event_photo,
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





