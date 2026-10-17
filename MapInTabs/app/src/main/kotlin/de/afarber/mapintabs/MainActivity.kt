package de.afarber.mapintabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.afarber.openmapview.CameraPosition
import de.afarber.openmapview.LatLng
import de.afarber.openmapview.compose.OpenMap
import de.afarber.openmapview.compose.rememberCameraPositionState
import java.util.Locale

data class BottomNavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
)

private val items =
    listOf(
        BottomNavigationItem("settings", "Einstellungen", Icons.Filled.Settings),
        BottomNavigationItem("map", "Karte", Icons.Filled.Place),
        BottomNavigationItem("data", "Daten", Icons.Filled.Done),
    )

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                TabsScreen()
            }
        }
    }
}

@Composable
fun TabsScreen() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    // Lives outside the NavHost, so the map tab finds the camera again after switching tabs.
    // It is saved like rememberSaveable, so it also survives Activity recreation.
    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition(LatLng(52.5200, 13.4050), 14f)
        }

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(item.title) },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(navController, startDestination = "settings", modifier = Modifier.padding(padding)) {
            composable("settings") { CenteredText("Einstellungen") }
            // A new map each time the tab is shown, released when the tab is left
            composable("map") { OpenMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState) }
            composable("data") {
                val camera = cameraPositionState.position
                CenteredText(
                    String.format(Locale.US, "Map center %.4f, %.4f\nzoom %.1f", camera.target.latitude, camera.target.longitude, camera.zoom),
                )
            }
        }
    }
}

@Composable
fun CenteredText(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, style = MaterialTheme.typography.headlineSmall)
    }
}
