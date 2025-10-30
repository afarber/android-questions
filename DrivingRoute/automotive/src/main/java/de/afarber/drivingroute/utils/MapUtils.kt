package de.afarber.drivingroute.utils

import de.afarber.openmapview.LatLng
import de.afarber.openmapview.LatLngBounds

object MapUtils {
    
    fun calculateBoundingBox(points: List<LatLng>): LatLngBounds {
        if (points.isEmpty()) {
            throw IllegalArgumentException("Points list cannot be empty")
        }

        val builder = LatLngBounds.builder()
        points.forEach { builder.include(it) }
        return builder.build()
    }
    
    fun createBoundingBoxWithPadding(
        start: LatLng,
        finish: LatLng,
        paddingFactor: Double = 1.2
    ): LatLngBounds {
        val points = listOf(start, finish)
        val bounds = calculateBoundingBox(points)

        val center = bounds.getCenter()
        val latSpan = bounds.northeast.latitude - bounds.southwest.latitude
        val lngSpan = bounds.northeast.longitude - bounds.southwest.longitude

        val newLatSpan = latSpan * paddingFactor
        val newLngSpan = lngSpan * paddingFactor

        val southwest = LatLng(
            center.latitude - newLatSpan / 2,
            center.longitude - newLngSpan / 2
        )
        val northeast = LatLng(
            center.latitude + newLatSpan / 2,
            center.longitude + newLngSpan / 2
        )

        return LatLngBounds(southwest, northeast)
    }
}