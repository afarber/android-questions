package de.afarber.composemap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import de.afarber.openmapview.CameraUpdateFactory
import de.afarber.openmapview.LatLng
import de.afarber.openmapview.MarkerOptions
import de.afarber.openmapview.OpenMapView

private val places =
    mapOf(
        "Reichstag" to LatLng(52.5186, 13.3762),
        "Alexanderplatz" to LatLng(52.5219, 13.4132),
        "Tempelhof" to LatLng(52.4731, 13.4039),
    )

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                var selected by remember { mutableStateOf(places.keys.first()) }

                Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        places.keys.forEach { name ->
                            FilterChip(
                                selected = name == selected,
                                onClick = { selected = name },
                                label = { Text(name) },
                            )
                        }
                    }
                    OpenStreetMap(center = places.getValue(selected), zoom = 15f, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
fun OpenStreetMap(
    center: LatLng,
    zoom: Float,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember { OpenMapView(context) }

    // Forward onResume, onPause, onDestroy of the Activity to the map
    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(mapView)
        onDispose { lifecycleOwner.lifecycle.removeObserver(mapView) }
    }

    AndroidView(
        // The map view would draw its tiles over the neighbor composables otherwise
        modifier = modifier.clipToBounds(),
        factory = {
            mapView.apply {
                setCenter(center)
                setZoom(zoom)
                places.forEach { (name, position) ->
                    addMarker(MarkerOptions().position(position).title(name))
                }
            }
        },
        // Called again whenever center or zoom change
        update = { it.animateCamera(CameraUpdateFactory.newLatLngZoom(center, zoom), 1000) },
    )
}
