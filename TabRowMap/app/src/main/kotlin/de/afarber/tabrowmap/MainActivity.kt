package de.afarber.tabrowmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.afarber.openmapview.CameraPosition
import de.afarber.openmapview.LatLng
import de.afarber.openmapview.compose.CameraPositionState
import de.afarber.openmapview.compose.Marker
import de.afarber.openmapview.compose.OpenMap
import de.afarber.openmapview.compose.rememberCameraPositionState
import de.afarber.openmapview.compose.rememberMarkerState

private val DEPOK = LatLng(-6.3970066, 106.8224316)

private val monitors = (1..12).map { "Monitor $it" to if (it % 4 == 0) "Offline" else "Online" }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold { padding ->
                    TabScreen(Modifier.padding(padding))
                }
            }
        }
    }
}

@Composable
fun TabScreen(modifier: Modifier = Modifier) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Map", "Monitors")

    // Hoisted above the tabs, so the map shows the same place after switching tabs
    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition(DEPOK, 12f)
        }

    Column(modifier = modifier.fillMaxWidth()) {
        SecondaryTabRow(
            selectedTabIndex = tabIndex,
            containerColor = Color.White,
            contentColor = Color(0xFFC2C2C2),
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabIndex),
                    height = 4.dp,
                    color = Color.Yellow,
                )
            },
            divider = { HorizontalDivider(color = Color(0xFFF4F4F4)) },
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color(0xFFC2C2C2),
                )
            }
        }

        when (tabIndex) {
            0 -> MapScreen(cameraPositionState)
            1 -> MonitorsScreen()
        }
    }
}

@Composable
fun MapScreen(cameraPositionState: CameraPositionState) {
    // No clipToBounds() and no lifecycle code: OpenMapView clips its drawing to its own
    // bounds, so it stays below the TabRow, and it cleans up when the tab is left
    OpenMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
    ) {
        Marker(state = rememberMarkerState(DEPOK), title = "Depok")
    }
}

@Composable
fun MonitorsScreen() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(monitors) { (name, status) ->
            ListItem(headlineContent = { Text(name) }, supportingContent = { Text(status) })
            HorizontalDivider()
        }
    }
}
