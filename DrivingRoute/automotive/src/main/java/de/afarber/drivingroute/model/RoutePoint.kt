package de.afarber.drivingroute.model

import org.osmdroid.util.GeoPoint

data class RoutePoint(
    val geoPoint: GeoPoint,
    val type: MarkerType
) {
    enum class MarkerType {
        START,
        FINISH
    }
}