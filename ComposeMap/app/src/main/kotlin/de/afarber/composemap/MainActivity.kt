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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.afarber.openmapview.CameraPosition
import de.afarber.openmapview.CameraUpdateFactory
import de.afarber.openmapview.LatLng
import de.afarber.openmapview.compose.Marker
import de.afarber.openmapview.compose.OpenMap
import de.afarber.openmapview.compose.rememberCameraPositionState
import de.afarber.openmapview.compose.rememberMarkerState

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
                MapScreen()
            }
        }
    }
}

@Composable
fun MapScreen() {
    var selected by rememberSaveable { mutableStateOf(places.keys.first()) }
    // The camera of the map, also kept when the Activity is recreated
    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition(places.getValue(selected), 15f)
        }

    // Fly to the selected place
    LaunchedEffect(selected) {
        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(places.getValue(selected), 15f), 1000)
    }

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

        OpenMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
        ) {
            places.forEach { (name, position) ->
                Marker(state = rememberMarkerState(position), title = name)
            }
        }
    }
}
