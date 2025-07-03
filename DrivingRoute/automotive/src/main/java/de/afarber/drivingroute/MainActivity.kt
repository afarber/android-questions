package de.afarber.drivingroute

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.afarber.drivingroute.model.AppState
import de.afarber.drivingroute.network.RouteRepository
import de.afarber.drivingroute.ui.MapController
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay

class MainActivity : AppCompatActivity(), MapEventsReceiver {
    
    private lateinit var mapView: MapView
    private lateinit var cancelFab: FloatingActionButton
    private lateinit var mapController: MapController
    private lateinit var routeRepository: RouteRepository
    
    private var currentState = AppState.IDLE
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configure osmdroid
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        
        setContentView(R.layout.activity_main)
        
        initViews()
        setupMapEvents()
        setupClickListeners()
        
        routeRepository = RouteRepository()
        
        Log.d(TAG, "MainActivity created, current state: $currentState")
    }
    
    private fun initViews() {
        mapView = findViewById(R.id.mapView)
        cancelFab = findViewById(R.id.cancelFab)
        
        mapController = MapController(this, mapView)
    }
    
    private fun setupMapEvents() {
        val mapEventsOverlay = MapEventsOverlay(this)
        mapView.overlays.add(mapEventsOverlay)
    }
    
    private fun setupClickListeners() {
        cancelFab.setOnClickListener {
            handleCancelClick()
        }
    }
    
    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
        p?.let { geoPoint ->
            handleMapTap(geoPoint)
        }
        return true
    }
    
    override fun longPressHelper(p: GeoPoint?): Boolean {
        // Not used in this implementation
        return false
    }
    
    private fun handleMapTap(geoPoint: GeoPoint) {
        Log.d(TAG, "Map tapped at: ${geoPoint.latitude}, ${geoPoint.longitude}, current state: $currentState")
        
        when (currentState) {
            AppState.IDLE -> {
                mapController.addStartMarker(geoPoint)
                updateState(AppState.START_MARKER)
            }
            AppState.START_MARKER -> {
                mapController.addFinishMarker(geoPoint)
                updateState(AppState.FINISH_MARKER)
                calculateRoute()
            }
            AppState.FINISH_MARKER, AppState.ROUTE_DISPLAYED -> {
                // In these states, map taps are ignored (user should use Cancel to reset)
                Toast.makeText(this, "Use Cancel button to place new markers", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun handleCancelClick() {
        Log.d(TAG, "Cancel clicked, current state: $currentState")
        
        mapController.clearAll()
        updateState(AppState.IDLE)
        
        Toast.makeText(this, "Markers and route cleared", Toast.LENGTH_SHORT).show()
    }
    
    private fun calculateRoute() {
        val startPos = mapController.getStartMarkerPosition()
        val finishPos = mapController.getFinishMarkerPosition()
        
        if (startPos == null || finishPos == null) {
            Log.e(TAG, "Cannot calculate route: missing marker positions")
            return
        }
        
        Log.d(TAG, "Calculating route from $startPos to $finishPos")
        
        lifecycleScope.launch {
            try {
                val result = routeRepository.getRoute(startPos, finishPos)
                
                result.onSuccess { response ->
                    if (response.routes.isNotEmpty()) {
                        val route = response.routes.first()
                        mapController.addRoute(route.geometry)
                        updateState(AppState.ROUTE_DISPLAYED)
                        
                        val distance = "%.1f km".format(route.distance / 1000)
                        val duration = "%d min".format((route.duration / 60).toInt())
                        Toast.makeText(this@MainActivity, "Route: $distance, $duration", Toast.LENGTH_LONG).show()
                        
                        Log.d(TAG, "Route calculated successfully: $distance, $duration")
                    } else {
                        showRouteError("No route found")
                    }
                }
                
                result.onFailure { exception ->
                    Log.e(TAG, "Route calculation failed", exception)
                    showRouteError("Failed to calculate route: ${exception.message}")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during route calculation", e)
                showRouteError("Unexpected error: ${e.message}")
            }
        }
    }
    
    private fun showRouteError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        // Stay in FINISH_MARKER state so user can try again or cancel
    }
    
    private fun updateState(newState: AppState) {
        Log.d(TAG, "State transition: $currentState -> $newState")
        currentState = newState
        updateUI()
    }
    
    private fun updateUI() {
        when (currentState) {
            AppState.IDLE -> {
                cancelFab.hide()
            }
            AppState.START_MARKER, AppState.FINISH_MARKER, AppState.ROUTE_DISPLAYED -> {
                cancelFab.show()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }
    
    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDetach()
    }
    
    companion object {
        private const val TAG = "MainActivity"
    }
}