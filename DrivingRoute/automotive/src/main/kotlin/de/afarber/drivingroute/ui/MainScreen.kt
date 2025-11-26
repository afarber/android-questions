package de.afarber.drivingroute.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import de.afarber.drivingroute.R
import de.afarber.drivingroute.model.AppState
import de.afarber.drivingroute.ui.theme.Red500

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val appState by viewModel.appState.collectAsState()
    val startMarker by viewModel.startMarker.collectAsState()
    val finishMarker by viewModel.finishMarker.collectAsState()
    val routePoints by viewModel.routePoints.collectAsState()
    val routeInfo by viewModel.routeInfo.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        floatingActionButton = {
            if (appState != AppState.IDLE) {
                FloatingActionButton(
                    onClick = { viewModel.clearAll() },
                    containerColor = androidx.compose.ui.graphics.Color(Red500.value)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Cancel",
                        tint = androidx.compose.ui.graphics.Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MapViewContainer(
                startMarker = startMarker,
                finishMarker = finishMarker,
                routePoints = routePoints,
                onMapClick = { latLng ->
                    viewModel.handleMapClick(latLng)
                },
                lifecycleOwner = lifecycleOwner
            )

            // Show route info card when route is available
            routeInfo?.let { info ->
                RouteInfoCard(
                    routeInfo = info,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                )
            }
        }
    }

    // AlertDialog shown when errorMessage is not null
    errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            },
            title = { Text("Notice") },
            text = { Text(message) }
        )
    }
}

