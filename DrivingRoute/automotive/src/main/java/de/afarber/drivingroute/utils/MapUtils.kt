package de.afarber.drivingroute.utils

import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint

object MapUtils {
    
    fun calculateBoundingBox(points: List<GeoPoint>): BoundingBox {
        if (points.isEmpty()) {
            throw IllegalArgumentException("Points list cannot be empty")
        }
        
        val minLat = points.minOf { it.latitude }
        val maxLat = points.maxOf { it.latitude }
        val minLon = points.minOf { it.longitude }
        val maxLon = points.maxOf { it.longitude }
        
        return BoundingBox(maxLat, maxLon, minLat, minLon)
    }
    
    fun createBoundingBoxWithPadding(start: GeoPoint, finish: GeoPoint, paddingFactor: Double = 1.2): BoundingBox {
        val points = listOf(start, finish)
        val boundingBox = calculateBoundingBox(points)
        return boundingBox.increaseByScale(paddingFactor.toFloat())
    }
}