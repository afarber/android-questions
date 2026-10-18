package de.afarber.darkmodemap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.afarber.openmapview.CameraPosition
import de.afarber.openmapview.LatLng
import de.afarber.openmapview.compose.ComposeMapColorScheme
import de.afarber.openmapview.compose.Marker
import de.afarber.openmapview.compose.OpenMap
import de.afarber.openmapview.compose.Polygon
import de.afarber.openmapview.compose.rememberCameraPositionState
import de.afarber.openmapview.compose.rememberMarkerState

private val BOCHUM = LatLng(51.4661, 7.2491)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val dark = isSystemInDarkTheme()
            MaterialTheme(colorScheme = if (dark) darkColorScheme() else lightColorScheme()) {
                MapScreen(useDarkScheme = dark)
            }
        }
    }
}

// No ViewModel holding the map and no onPause()/onResume() calls: switching dark mode
// recreates the Activity, and with it a new map view. The camera and the polygon points
// are saved state (LatLng is Parcelable), so the new map shows the same place and polygon.
@Composable
fun MapScreen(useDarkScheme: Boolean) {
    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition(BOCHUM, 13f)
        }
    var points by rememberSaveable { mutableStateOf(emptyList<LatLng>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        OpenMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            // Dark tiles without an API key, on every Android version. Only the tiles are
            // darkened, the markers and the polygon keep their colors. For a designed dark
            // style use MapType.CARTO_DARK, which needs a CARTO API key.
            mapColorScheme = if (useDarkScheme) ComposeMapColorScheme.DARK else ComposeMapColorScheme.LIGHT,
            onMapClick = { points = points + it },
        ) {
            points.forEach { Marker(state = rememberMarkerState(it)) }
            if (points.size >= 3) {
                Polygon(
                    points = points,
                    strokeColor = Color(0xFF1565C0),
                    strokeWidth = 8f,
                    fillColor = Color(0x551E88E5),
                )
            }
        }
        Surface(
            modifier = Modifier.align(Alignment.TopStart).safeDrawingPadding().padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
        ) {
            Text(
                text = "${if (useDarkScheme) "Dark" else "Light"} mode - tap to add corners: ${points.size}",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}
