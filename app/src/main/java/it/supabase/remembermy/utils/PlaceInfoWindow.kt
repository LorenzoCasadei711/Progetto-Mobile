package com.example.progettomobile.utils
import android.widget.ImageView
import android.widget.TextView
import coil.load
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow
import it.supabase.remembermy.R

class PlaceInfoWindow(
    mapView: MapView,
    private val imageUrl: String?,
    private val description: String
) : InfoWindow(R.layout.marker_info_window, mapView) {

    override fun onOpen(item: Any?) {
        val marker = item as Marker
        val imageView = mView.findViewById<ImageView>(R.id.placeImage)
        val titleView = mView.findViewById<TextView>(R.id.placeTitle)
        val descriptionView = mView.findViewById<TextView>(R.id.placeDescription)

        imageView.load(imageUrl)
        titleView.text = marker.title
        descriptionView.text = description

        mView.setOnClickListener {
            close()
        }
    }

    override fun onClose() {}
}