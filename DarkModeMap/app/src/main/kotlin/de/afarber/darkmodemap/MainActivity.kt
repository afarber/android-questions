package de.afarber.darkmodemap

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.RenderEffect
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import de.afarber.openmapview.CameraPosition
import de.afarber.openmapview.LatLng
import de.afarber.openmapview.compose.Marker
import de.afarber.openmapview.compose.OpenMap
import de.afarber.openmapview.compose.Polygon
import de.afarber.openmapview.compose.rememberCameraPositionState
import de.afarber.openmapview.compose.rememberMarkerState

private val BOCHUM = LatLng(51.4661, 7.2491)

// LatLng is not Parcelable, so the points are saved as latitude, longitude, latitude, ...
private val LatLngListSaver =
    Saver<List<LatLng>, List<Double>>(
        save = { points -> points.flatMap { listOf(it.latitude, it.longitude) } },
        restore = { values -> values.chunked(2) { LatLng(it[0], it[1]) } },
    )

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
// are saved state, so the new map shows the same place and polygon.
@Composable
fun MapScreen(useDarkScheme: Boolean) {
    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition(BOCHUM, 13f)
        }
    var points by rememberSaveable(stateSaver = LatLngListSaver) { mutableStateOf(emptyList<LatLng>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        OpenMap(
            modifier = Modifier.fillMaxSize().then(darkTiles(useDarkScheme)),
            cameraPositionState = cameraPositionState,
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

// A workaround, not a real dark map style: the OSM tiles are inverted and their hue is
// turned by 180 degrees, so roads and water keep roughly their colors. It also changes the
// markers and the polygon, and needs Android 12. A real dark style is MapType.CARTO_DARK,
// which needs a CARTO API key.
private fun darkTiles(useDarkScheme: Boolean): Modifier =
    if (useDarkScheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Modifier.graphicsLayer { renderEffect = invertedColors() }
    } else {
        Modifier
    }

@RequiresApi(Build.VERSION_CODES.S)
private fun invertedColors() =
    RenderEffect
        .createColorFilterEffect(
            ColorMatrixColorFilter(
                ColorMatrix(
                    floatArrayOf(
                        -1f, 0f, 0f, 0f, 255f,
                        0f, -1f, 0f, 0f, 255f,
                        0f, 0f, -1f, 0f, 255f,
                        0f, 0f, 0f, 1f, 0f,
                    ),
                ).apply {
                    // Hue rotation by 180 degrees, applied after the inversion
                    postConcat(
                        ColorMatrix(
                            floatArrayOf(
                                -0.574f, 1.430f, 0.144f, 0f, 0f,
                                0.426f, 0.430f, 0.144f, 0f, 0f,
                                0.426f, 1.430f, -0.856f, 0f, 0f,
                                0f, 0f, 0f, 1f, 0f,
                            ),
                        ),
                    )
                },
            ),
        ).asComposeRenderEffect()
