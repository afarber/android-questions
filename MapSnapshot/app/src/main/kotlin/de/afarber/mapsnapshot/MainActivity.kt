package de.afarber.mapsnapshot

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import de.afarber.openmapview.CameraUpdateFactory
import de.afarber.openmapview.LatLng
import de.afarber.openmapview.LatLngBounds
import de.afarber.openmapview.OpenMapView
import de.afarber.openmapview.Polyline
import java.io.File

// A short run through the Tiergarten park in Berlin
private val RUN_TRACK =
    listOf(
        LatLng(52.5145, 13.3501),
        LatLng(52.5163, 13.3610),
        LatLng(52.5163, 13.3720),
        LatLng(52.5130, 13.3735),
        LatLng(52.5105, 13.3650),
        LatLng(52.5110, 13.3540),
        LatLng(52.5145, 13.3501),
    )

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                RunScreen()
            }
        }
    }
}

@Composable
fun RunScreen() {
    val context = LocalContext.current
    var isRunFinished by remember { mutableStateOf(false) }
    var snapshot by remember { mutableStateOf<Bitmap?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        TrackerMap(
            isRunFinished = isRunFinished,
            locations = RUN_TRACK,
            onSnapshot = { bitmap ->
                // Save the map screenshot as PNG file into the app storage
                File(context.filesDir, "run.png").outputStream().use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                snapshot = bitmap
            },
        )

        Button(
            onClick = { isRunFinished = true },
            enabled = !isRunFinished,
            modifier = Modifier.align(Alignment.BottomCenter).safeDrawingPadding().padding(16.dp),
        ) {
            Text("Finish run")
        }

        snapshot?.let { bitmap ->
            Card(modifier = Modifier.align(Alignment.TopEnd).safeDrawingPadding().padding(16.dp)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Saved run.png", style = MaterialTheme.typography.labelLarge)
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Map snapshot",
                        modifier = Modifier.width(140.dp).border(1.dp, Color.Gray),
                    )
                }
            }
        }
    }
}

@Composable
fun TrackerMap(
    isRunFinished: Boolean,
    locations: List<LatLng>,
    onSnapshot: (Bitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember { OpenMapView(context) }

    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(mapView)
        onDispose { lifecycleOwner.lifecycle.removeObserver(mapView) }
    }

    LaunchedEffect(isRunFinished) {
        if (isRunFinished) {
            mapView.snapshot { bitmap -> bitmap?.let(onSnapshot) }
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = {
            mapView.apply {
                // Show the whole run, after the map view has got its size
                post {
                    val bounds = LatLngBounds.builder().apply { locations.forEach { include(it) } }.build()
                    moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                }
            }
        },
        update = { view ->
            view.clearPolylines()
            view.addPolyline(Polyline(points = locations, strokeColor = Color(0xFFE53935), strokeWidth = 12f))
        },
    )
}
