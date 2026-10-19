package de.afarber.curvedline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.afarber.openmapview.CameraPosition
import de.afarber.openmapview.Dash
import de.afarber.openmapview.Gap
import de.afarber.openmapview.LatLng
import de.afarber.openmapview.compose.Marker
import de.afarber.openmapview.compose.OpenMap
import de.afarber.openmapview.compose.Polyline
import de.afarber.openmapview.compose.rememberCameraPositionState
import de.afarber.openmapview.compose.rememberMarkerState

private val FRANKFURT = LatLng(50.0379, 8.5622)
private val NEW_YORK = LatLng(40.6413, -73.7781)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                CurvedLineScreen()
            }
        }
    }
}

@Composable
fun CurvedLineScreen() {
    val cameraPositionState = rememberCameraPositionState { position = CameraPosition(LatLng(48.0, -32.0), 3f) }
    val start = rememberMarkerState(NEW_YORK)
    val end = rememberMarkerState(FRANKFURT)

    Box(modifier = Modifier.fillMaxSize()) {
        OpenMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
        ) {
            // Straight on the screen
            Polyline(points = listOf(start.position, end.position), strokeColor = Color.Gray, strokeWidth = 6f)
            // The shortest way on the earth, a great circle, curved on the map
            Polyline(
                points = listOf(start.position, end.position),
                geodesic = true,
                strokeColor = Color(0xFF1565C0),
                strokeWidth = 10f,
            )
            // A curve of your own, from the computed points of a Bezier arc
            Polyline(
                points = bezierArc(start.position, end.position),
                pattern = listOf(Dash(30f), Gap(20f)),
                strokeColor = Color(0xFFE65100),
                strokeWidth = 10f,
            )
            // The positions change while the markers are dragged, and the lines follow
            Marker(state = start, title = "Start", draggable = true)
            Marker(state = end, title = "End", draggable = true)
        }
        Surface(
            modifier = Modifier.align(Alignment.TopStart).safeDrawingPadding().padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
        ) {
            Text(
                text = "Gray: straight   Blue: geodesic   Orange: Bezier arc - drag the markers",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

// A quadratic Bezier curve from start to end, bent to the left of the way: the control point
// is beside the midpoint, at a quarter of the distance. Computed in degrees, like the map draws it.
fun bezierArc(
    start: LatLng,
    end: LatLng,
    steps: Int = 50,
): List<LatLng> {
    val dLat = end.latitude - start.latitude
    val dLng = end.longitude - start.longitude
    val control = LatLng((start.latitude + end.latitude) / 2 + dLng / 4, (start.longitude + end.longitude) / 2 - dLat / 4)
    return (0..steps).map { i ->
        val t = i.toDouble() / steps
        val a = (1 - t) * (1 - t)
        val b = 2 * (1 - t) * t
        val c = t * t
        LatLng(
            a * start.latitude + b * control.latitude + c * end.latitude,
            a * start.longitude + b * control.longitude + c * end.longitude,
        )
    }
}
