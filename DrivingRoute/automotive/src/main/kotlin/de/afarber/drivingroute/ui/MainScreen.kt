package de.afarber.drivingroute.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.afarber.drivingroute.R
import de.afarber.drivingroute.model.AppState
import de.afarber.drivingroute.ui.theme.Red500
import de.afarber.openmapview.CameraPosition
import de.afarber.openmapview.CameraUpdateFactory
import de.afarber.openmapview.LatLng
import de.afarber.openmapview.compose.rememberCameraPositionState
import kotlinx.coroutines.launch


private const val LOG_TAG = "MainScreen"

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    // The camera starts over Wolfsburg and is kept when the Activity is recreated
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(LatLng(52.4227, 10.7865), 15f)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var isSnackbarVisible by remember { mutableStateOf(false) }

    val appState by viewModel.appState.collectAsState()
    val startMarker by viewModel.startMarker.collectAsState()
    val finishMarker by viewModel.finishMarker.collectAsState()
    val routePoints by viewModel.routePoints.collectAsState()
    val routeInfo by viewModel.routeInfo.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        floatingActionButton = {
            if (appState != AppState.IDLE && !isSnackbarVisible) {
                FloatingActionButton(
                    onClick = { viewModel.clearAll() },
                    containerColor = Red500
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Cancel",
                        tint = Color.White
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MapViewContainer(
                cameraPositionState = cameraPositionState,
                startMarker = startMarker,
                finishMarker = finishMarker,
                routePoints = routePoints,
                onMapClick = { latLng ->
                    viewModel.handleMapClick(latLng)
                },
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

            // Zoom toolbar in bottom left
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 6.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row {
                    FilledIconButton(
                        onClick = { scope.launch { cameraPositionState.animate(CameraUpdateFactory.zoomOut()) } },
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Zoom out"
                        )
                    }
                    FilledIconButton(
                        onClick = { scope.launch { cameraPositionState.animate(CameraUpdateFactory.zoomIn()) } },
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Zoom in"
                        )
                    }
                }
            }
        }
    }

    // Show snack bar when errorMessage is not null
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            isSnackbarVisible = true
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "OK",
                duration = SnackbarDuration.Short
            )
            isSnackbarVisible = false
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    Log.d(LOG_TAG, "Snackbar ActionPerformed")
                    // viewModel.retry()
                }
                SnackbarResult.Dismissed -> {
                    Log.d(LOG_TAG, "Snackbar Dismissed")
                }
            }
            viewModel.clearError()
        }
    }
}

