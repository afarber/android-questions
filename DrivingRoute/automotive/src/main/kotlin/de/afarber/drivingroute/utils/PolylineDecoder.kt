package de.afarber.drivingroute.utils

import de.afarber.openmapview.LatLng

object PolylineDecoder {
    
    fun decode(polyline: String): List<LatLng> {
        val coordinates = mutableListOf<LatLng>()
        var index = 0
        val len = polyline.length
        var lat = 0
        var lng = 0
        
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            
            do {
                b = polyline[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            
            shift = 0
            result = 0
            
            do {
                b = polyline[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            
            coordinates.add(LatLng(lat / 1E5, lng / 1E5))
        }
        
        return coordinates
    }
}