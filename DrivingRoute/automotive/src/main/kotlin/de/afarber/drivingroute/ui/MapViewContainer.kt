package de.afarber.drivingroute.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import de.afarber.drivingroute.ui.theme.RouteBlue
import de.afarber.drivingroute.utils.MapUtils
import de.afarber.openmapview.BitmapDescriptorFactory
import de.afarber.openmapview.CameraUpdateFactory
import de.afarber.openmapview.LatLng
import de.afarber.openmapview.Marker
import de.afarber.openmapview.OpenMapView
import de.afarber.openmapview.Polyline

@Composable
fun MapViewContainer(
    startMarker: LatLng?,
    finishMarker: LatLng?,
    routePoints: List<LatLng>,
    onMapClick: (LatLng) -> Unit,
    onZoomIn: (() -> Unit) -> Unit,
    onZoomOut: (() -> Unit) -> Unit,
    lifecycleOwner: LifecycleOwner
) {
    AndroidView(
        factory = { ctx ->
            OpenMapView(ctx).apply {
                lifecycleOwner.lifecycle.addObserver(this)
                setZoom(15.0f)
                setCenter(LatLng(52.4227, 10.7865))

                setOnMapClickListener { latLng ->
                    onMapClick(latLng)
                }

                setOnMarkerClickListener { marker ->
                    true
                }

                // Provide zoom functions to parent by calling callbacks with lambdas
                onZoomIn { animateCamera(CameraUpdateFactory.zoomIn()) }
                onZoomOut { animateCamera(CameraUpdateFactory.zoomOut()) }
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
