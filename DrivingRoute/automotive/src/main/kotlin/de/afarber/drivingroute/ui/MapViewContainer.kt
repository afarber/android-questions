package de.afarber.drivingroute.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import de.afarber.drivingroute.ui.theme.RouteBlue
import de.afarber.drivingroute.utils.MapUtils
import de.afarber.openmapview.BitmapDescriptorFactory
import de.afarber.openmapview.CameraUpdateFactory
import de.afarber.openmapview.LatLng
import de.afarber.openmapview.compose.CameraPositionState
import de.afarber.openmapview.compose.Marker
import de.afarber.openmapview.compose.MarkerState
import de.afarber.openmapview.compose.OpenMap
import de.afarber.openmapview.compose.Polyline

@Composable
fun MapViewContainer(
    cameraPositionState: CameraPositionState,
    startMarker: LatLng?,
    finishMarker: LatLng?,
    routePoints: List<LatLng>,
    onMapClick: (LatLng) -> Unit,
) {
    // Fit the route into view when it arrives
    LaunchedEffect(routePoints) {
        if (routePoints.isNotEmpty() && startMarker != null && finishMarker != null) {
            val bounds = MapUtils.createBoundingBoxWithPadding(startMarker, finishMarker, 1.3)
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }

    OpenMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = onMapClick,
    ) {
        startMarker?.let { start ->
            Marker(
                state = remember(start) { MarkerState(start) },
                title = "Start",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                // No info window, a tap places the next marker instead
                onClick = { true },
            )
        }

        finishMarker?.let { finish ->
            Marker(
                state = remember(finish) { MarkerState(finish) },
                title = "Finish",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                onClick = { true },
            )
        }

        if (routePoints.isNotEmpty()) {
            Polyline(points = routePoints, strokeColor = RouteBlue, strokeWidth = 8f)
        }
    }
}
