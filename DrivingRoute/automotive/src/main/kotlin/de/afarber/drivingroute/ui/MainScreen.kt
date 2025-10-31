package de.afarber.drivingroute.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import de.afarber.drivingroute.R
import de.afarber.drivingroute.model.AppState
import de.afarber.drivingroute.ui.theme.Red500
import de.afarber.drivingroute.ui.theme.RouteBlue
import de.afarber.drivingroute.utils.MapUtils
import de.afarber.openmapview.BitmapDescriptorFactory
import de.afarber.openmapview.CameraUpdateFactory
import de.afarber.openmapview.LatLng
import de.afarber.openmapview.Marker
import de.afarber.openmapview.OpenMapView
import de.afarber.openmapview.Polyline

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

    val appState by viewModel.appState.collectAsState()
    val startMarker by viewModel.startMarker.collectAsState()
    val finishMarker by viewModel.finishMarker.collectAsState()
    val routePoints by viewModel.routePoints.collectAsState()
    val routeInfo by viewModel.routeInfo.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(routeInfo) {
        routeInfo?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
        }
    }
}

@Composable
fun MapViewContainer(
    startMarker: LatLng?,
    finishMarker: LatLng?,
    routePoints: List<LatLng>,
    onMapClick: (LatLng) -> Unit,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            OpenMapView(ctx).apply {
                lifecycleOwner.lifecycle.addObserver(this)
                // Map type is set to default (STANDARD) automatically
                setZoom(15.0)
                setCenter(LatLng(52.4227, 10.7865))
                setMinZoomPreference(3.0f)
                setMaxZoomPreference(20.0f)

                setOnMapClickListener { latLng ->
                    onMapClick(latLng)
                }

                setOnMarkerClickListener { marker ->
                    true
                }
            }
        },
        update = { mapView ->
            mapView.clearMarkers()
            mapView.clearPolylines()

            startMarker?.let { start ->
                mapView.addMarker(
                    Marker(
                        position = start,
                        title = "Start",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                    )
                )
            }

            finishMarker?.let { finish ->
                mapView.addMarker(
                    Marker(
                        position = finish,
                        title = "Finish",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )
                )
            }

            if (routePoints.isNotEmpty()) {
                mapView.addPolyline(
                    Polyline(
                        points = routePoints,
                        strokeColor = RouteBlue,
                        strokeWidth = 8f
                    )
                )

                if (startMarker != null && finishMarker != null) {
                    val bounds = MapUtils.createBoundingBoxWithPadding(startMarker, finishMarker, 1.3)
                    mapView.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(bounds, 100)
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

