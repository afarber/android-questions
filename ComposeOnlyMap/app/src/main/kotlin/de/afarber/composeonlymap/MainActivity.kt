package de.afarber.composeonlymap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.afarber.openmapview.CameraPosition
import de.afarber.openmapview.LatLng
import de.afarber.openmapview.compose.Marker
import de.afarber.openmapview.compose.OpenMap
import de.afarber.openmapview.compose.rememberCameraPositionState
import de.afarber.openmapview.compose.rememberMarkerState

private val ISFAHAN = LatLng(32.627387981943, 51.62582233021288)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // No XML layout, no Configuration.load() and no User-Agent setup: OpenMapView
        // sends a User-Agent with the app's package name, as the OSM tile servers require
        setContent {
            MaterialTheme {
                MapCarScreen()
            }
        }
    }
}

@Composable
fun MapCarScreen() {
    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition(ISFAHAN, 9.5f)
        }

    Box(modifier = Modifier.fillMaxSize()) {
        OpenMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
        ) {
            Marker(state = rememberMarkerState(ISFAHAN), title = "Isfahan")
        }
    }
}
