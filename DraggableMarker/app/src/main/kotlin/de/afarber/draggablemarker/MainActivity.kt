package de.afarber.draggablemarker

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import de.afarber.openmapview.BitmapDescriptorFactory
import de.afarber.openmapview.LatLng
import de.afarber.openmapview.Marker
import de.afarber.openmapview.MarkerOptions
import de.afarber.openmapview.OnMarkerDragListener
import de.afarber.openmapview.OpenMapView
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var mapView: OpenMapView
    private lateinit var positionText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        positionText = findViewById(R.id.positionText)
        mapView = findViewById(R.id.mapView)
        lifecycle.addObserver(mapView)

        val thePoint = LatLng(52.5163, 13.3777)
        mapView.setZoom(15f)
        mapView.setCenter(thePoint)

        val marker =
            mapView.addMarker(
                MarkerOptions()
                    .position(thePoint)
                    .title("Brandenburg Gate")
                    .icon(BitmapDescriptorFactory.fromBitmap(ContextCompat.getDrawable(this, R.drawable.ic_pin)!!.toBitmap()))
                    .anchor(0.5f, 1.0f)
                    .draggable(true),
            )
        showPosition("Drag me", marker)

        mapView.setOnMarkerDragListener(
            object : OnMarkerDragListener {
                override fun onMarkerDragStart(marker: Marker) = showPosition("Drag started", marker)

                override fun onMarkerDrag(marker: Marker) = showPosition("Dragging", marker)

                override fun onMarkerDragEnd(marker: Marker) = showPosition("Dropped", marker)
            },
        )
    }

    private fun showPosition(
        state: String,
        marker: Marker,
    ) {
        positionText.text =
            String.format(Locale.US, "%s: %.5f, %.5f", state, marker.position.latitude, marker.position.longitude)
    }
}
