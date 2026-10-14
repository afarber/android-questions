package de.afarber.zerosetupmap

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import de.afarber.openmapview.LatLng
import de.afarber.openmapview.OpenMapView
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var map: OpenMapView
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No Configuration.load(), no storage permissions and no tile cache path to set up
        setContentView(R.layout.main)

        statusText = findViewById(R.id.statusText)
        map = findViewById(R.id.map)
        lifecycle.addObserver(map)

        map.setCenter(LatLng(51.4779, -0.0015))
        map.setZoom(15f)

        statusText.text = "Loading tiles..."
        map.setOnMapLoadedCallback {
            // Downloaded tiles are cached in the app's own cache dir, which needs no permission
            val bytes = cacheDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
            statusText.text = String.format(Locale.US, "Map loaded, tile cache: %.1f KB", bytes / 1024.0)
        }
    }
}
