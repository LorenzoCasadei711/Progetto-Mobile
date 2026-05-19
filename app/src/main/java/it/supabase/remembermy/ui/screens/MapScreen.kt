package ui.screens

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.example.progettomobile.utils.PlaceInfoWindow
import com.example.progettomobile.R
@Composable
fun MapScreen(navController: NavController) {
    AndroidView(
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
                val puntiInteresse = listOf(
                    GeoPoint(44.1391, 12.2431) to "Cesena centro",
                    GeoPoint(44.1464, 12.2362) to "Stazione di Cesena",
                    GeoPoint(44.1376, 12.2464) to "Biblioteca Malatestiana"
                )
                val mapView = this
                puntiInteresse.forEach { (posizione, nome) ->
                    val marker = Marker(this).apply {
                        position = posizione
                        title = nome
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        infoWindow = PlaceInfoWindow(
                            mapView = mapView,
                            imageResId = R.drawable.cesena_centro,
                            description = "Centro storico di Cesena"
                        )
                    }

                    overlays.add(marker)
                }

                invalidate()
            }
        }
    )
}