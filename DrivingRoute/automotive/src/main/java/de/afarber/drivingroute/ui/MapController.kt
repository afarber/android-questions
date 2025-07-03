package de.afarber.drivingroute.ui

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import de.afarber.drivingroute.R
import de.afarber.drivingroute.model.RoutePoint
import de.afarber.drivingroute.utils.MapUtils
import de.afarber.drivingroute.utils.PolylineDecoder
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class MapController(
    private val context: Context,
    private val mapView: MapView
) {
    
    private var startMarker: Marker? = null
    private var finishMarker: Marker? = null
    private var routePolyline: Polyline? = null
    
    init {
        setupMap()
    }
    
    private fun setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)
        
        // Set initial center to Wolfsburg
        val wolfsburgCenter = GeoPoint(52.4227, 10.7865)
        mapView.controller.setCenter(wolfsburgCenter)
        
        // Configure zoom levels
        mapView.minZoomLevel = 3.0
        mapView.maxZoomLevel = 20.0
    }
    
    fun addStartMarker(geoPoint: GeoPoint) {
        removeMarker(startMarker)
        
        startMarker = Marker(mapView).apply {
            position = geoPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = createMarkerIcon("S", ContextCompat.getColor(context, R.color.green_500))
            title = "Start"
        }
        
        mapView.overlays.add(startMarker)
        mapView.invalidate()
    }
    
    fun addFinishMarker(geoPoint: GeoPoint) {
        removeMarker(finishMarker)
        
        finishMarker = Marker(mapView).apply {
            position = geoPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = createMarkerIcon("F", ContextCompat.getColor(context, R.color.red_500))
            title = "Finish"
        }
        
        mapView.overlays.add(finishMarker)
        mapView.invalidate()
    }
    
    fun addRoute(encodedPolyline: String) {
        removePolyline()
        
        val routePoints = PolylineDecoder.decode(encodedPolyline)
        
        routePolyline = Polyline().apply {
            setPoints(routePoints)
            outlinePaint.apply {
                color = ContextCompat.getColor(context, R.color.route_color)
                strokeWidth = 8f
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
            }
        }
        
        mapView.overlays.add(0, routePolyline) // Add at index 0 to draw under markers
        mapView.invalidate()
        
        // Auto-zoom to fit route and markers
        autoZoomToFitMarkersAndRoute()
    }
    
    private fun autoZoomToFitMarkersAndRoute() {
        val start = startMarker?.position
        val finish = finishMarker?.position
        
        if (start != null && finish != null) {
            val boundingBox = MapUtils.createBoundingBoxWithPadding(start, finish, 1.3)
            mapView.zoomToBoundingBox(boundingBox, true, 100)
        }
    }
    
    fun clearAll() {
        removeMarker(startMarker)
        removeMarker(finishMarker)
        removePolyline()
        mapView.invalidate()
    }
    
    private fun removeMarker(marker: Marker?) {
        marker?.let { mapView.overlays.remove(it) }
    }
    
    private fun removePolyline() {
        routePolyline?.let { mapView.overlays.remove(it) }
        routePolyline = null
    }
    
    private fun createMarkerIcon(text: String, color: Int): android.graphics.drawable.Drawable {
        val size = 72
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        val paint = Paint().apply {
            isAntiAlias = true
        }
        
        // Draw circle
        paint.color = color
        paint.style = Paint.Style.FILL
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4, paint)
        
        // Draw border
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4, paint)
        
        // Draw text
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        paint.textSize = 32f
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        
        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)
        val textY = size / 2f + textBounds.height() / 2f
        canvas.drawText(text, size / 2f, textY, paint)
        
        return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
    }
    
    fun getStartMarkerPosition(): GeoPoint? = startMarker?.position
    fun getFinishMarkerPosition(): GeoPoint? = finishMarker?.position
}