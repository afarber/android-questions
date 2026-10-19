package de.afarber.movingmarker

import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.afarber.openmapview.BitmapDescriptorFactory
import de.afarber.openmapview.CameraPosition
import de.afarber.openmapview.LatLng
import de.afarber.openmapview.compose.Marker
import de.afarber.openmapview.compose.OpenMap
import de.afarber.openmapview.compose.Polyline
import de.afarber.openmapview.compose.rememberCameraPositionState
import de.afarber.openmapview.compose.rememberMarkerState

// A car route from Potsdamer Platz to Alexanderplatz in Berlin, from the public OSRM server
private val ROUTE =
    listOf(
        LatLng(52.509959, 13.376710),
        LatLng(52.510176, 13.376714),
        LatLng(52.512279, 13.377175),
        LatLng(52.513173, 13.377216),
        LatLng(52.514585, 13.377467),
        LatLng(52.514694, 13.379160),
        LatLng(52.515157, 13.381671),
        LatLng(52.515451, 13.386058),
        LatLng(52.516670, 13.385822),
        LatLng(52.517138, 13.392965),
        LatLng(52.517236, 13.393421),
        LatLng(52.517587, 13.398930),
        LatLng(52.517681, 13.399544),
        LatLng(52.518023, 13.400503),
        LatLng(52.519055, 13.403118),
        LatLng(52.522634, 13.410171),
        LatLng(52.523893, 13.412151),
        LatLng(52.523864, 13.412333),
    )

// A simulated location update every second, 30 m further along the route
private const val FIX_INTERVAL_MS = 1000
private const val FIX_STEP_METERS = 30f

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                DrivingScreen()
            }
        }
    }
}

@Composable
fun DrivingScreen() {
    val route = remember { Route(ROUTE) }
    val cameraPositionState = rememberCameraPositionState { position = CameraPosition(ROUTE.first(), 17f) }
    val markerState = rememberMarkerState(ROUTE.first())
    var heading by remember { mutableFloatStateOf(route.headingAt(0f)) }

    LaunchedEffect(route) {
        val driven = Animatable(0f)
        while (true) {
            // A new location fix arrived: glide from the last one to it until the next fix is due.
            // The marker and the camera are set from the same value in the same frame, so they
            // cannot get out of sync, as two separate animations would.
            val next = driven.value + FIX_STEP_METERS
            driven.animateTo(next, tween(FIX_INTERVAL_MS, easing = LinearEasing)) {
                val position = route.positionAt(value)
                markerState.position = position
                heading = route.headingAt(value)
                cameraPositionState.position = CameraPosition(position, cameraPositionState.position.zoom)
            }
            if (next >= route.length) driven.snapTo(0f)
        }
    }

    OpenMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
    ) {
        Polyline(points = ROUTE, strokeColor = Color(0xFF42A5F5), strokeWidth = 14f)
        Marker(
            state = markerState,
            icon = BitmapDescriptorFactory.fromResource(R.drawable.arrow),
            anchor = 0.5f to 0.5f,
            rotation = heading,
        )
    }
}

// Distances and headings along a route, with the platform's Location.distanceBetween()
private class Route(
    private val points: List<LatLng>,
) {
    private val segmentLengths = FloatArray(points.size - 1)
    private val segmentHeadings = FloatArray(points.size - 1)
    val length: Float

    init {
        val results = FloatArray(2)
        for (i in segmentLengths.indices) {
            val a = points[i]
            val b = points[i + 1]
            Location.distanceBetween(a.latitude, a.longitude, b.latitude, b.longitude, results)
            segmentLengths[i] = results[0]
            segmentHeadings[i] = results[1]
        }
        length = segmentLengths.sum()
    }

    fun positionAt(distance: Float): LatLng {
        var rest = distance.coerceIn(0f, length)
        for (i in segmentLengths.indices) {
            if (rest <= segmentLengths[i]) {
                val f = (rest / segmentLengths[i]).toDouble()
                val a = points[i]
                val b = points[i + 1]
                return LatLng(a.latitude + (b.latitude - a.latitude) * f, a.longitude + (b.longitude - a.longitude) * f)
            }
            rest -= segmentLengths[i]
        }
        return points.last()
    }

    fun headingAt(distance: Float): Float {
        var rest = distance.coerceIn(0f, length)
        for (i in segmentLengths.indices) {
            if (rest < segmentLengths[i]) return segmentHeadings[i]
            rest -= segmentLengths[i]
        }
        return segmentHeadings.last()
    }
}
