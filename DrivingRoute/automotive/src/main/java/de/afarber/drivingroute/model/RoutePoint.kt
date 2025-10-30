package de.afarber.drivingroute.model

import de.afarber.openmapview.LatLng

data class RoutePoint(
    val geoPoint: LatLng,
    val type: MarkerType
) {
    enum class MarkerType {
        START,
        FINISH
    }
}