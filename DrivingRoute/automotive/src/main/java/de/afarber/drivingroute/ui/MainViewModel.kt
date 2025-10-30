package de.afarber.drivingroute.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.afarber.drivingroute.model.AppState
import de.afarber.drivingroute.network.RouteRepository
import de.afarber.openmapview.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val routeRepository = RouteRepository()

    private val _appState = MutableStateFlow(AppState.IDLE)
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    private val _startMarker = MutableStateFlow<LatLng?>(null)
    val startMarker: StateFlow<LatLng?> = _startMarker.asStateFlow()

    private val _finishMarker = MutableStateFlow<LatLng?>(null)
    val finishMarker: StateFlow<LatLng?> = _finishMarker.asStateFlow()

    private val _routePoints = MutableStateFlow<List<LatLng>>(emptyList())
    val routePoints: StateFlow<List<LatLng>> = _routePoints.asStateFlow()

    private val _routeInfo = MutableStateFlow<String?>(null)
    val routeInfo: StateFlow<String?> = _routeInfo.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun handleMapClick(latLng: LatLng) {
        Log.d(TAG, "Map clicked at: ${latLng.latitude}, ${latLng.longitude}, state: ${_appState.value}")

        when (_appState.value) {
            AppState.IDLE -> {
                _startMarker.value = latLng
                _appState.value = AppState.START_MARKER
                Log.d(TAG, "Start marker placed")
            }
            AppState.START_MARKER -> {
                _finishMarker.value = latLng
                _appState.value = AppState.FINISH_MARKER
                Log.d(TAG, "Finish marker placed, calculating route...")
                calculateRoute()
            }
            AppState.FINISH_MARKER, AppState.ROUTE_DISPLAYED -> {
                _errorMessage.value = "Use Cancel button to place new markers"
            }
        }
    }

    fun clearAll() {
        Log.d(TAG, "Clearing all markers and route")
        _startMarker.value = null
        _finishMarker.value = null
        _routePoints.value = emptyList()
        _routeInfo.value = null
        _errorMessage.value = null
        _appState.value = AppState.IDLE
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun calculateRoute() {
        val start = _startMarker.value
        val finish = _finishMarker.value

        if (start == null || finish == null) {
            Log.e(TAG, "Cannot calculate route: missing marker positions")
            return
        }

        Log.d(TAG, "Calculating route from $start to $finish")

        viewModelScope.launch {
            try {
                val result = routeRepository.getRoute(start, finish)

                result.onSuccess { response ->
                    if (response.routes.isNotEmpty()) {
                        val route = response.routes.first()
                        _routePoints.value = de.afarber.drivingroute.utils.PolylineDecoder.decode(route.geometry)
                        _appState.value = AppState.ROUTE_DISPLAYED

                        val distance = "%.1f km".format(route.distance / 1000)
                        val duration = "%d min".format((route.duration / 60).toInt())
                        _routeInfo.value = "Route: $distance, $duration"

                        Log.d(TAG, "Route calculated successfully: $distance, $duration")
                    } else {
                        _errorMessage.value = "No route found"
                        Log.w(TAG, "No route found in response")
                    }
                }

                result.onFailure { exception ->
                    Log.e(TAG, "Route calculation failed", exception)
                    _errorMessage.value = "Failed to calculate route: ${exception.message}"
                }

            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during route calculation", e)
                _errorMessage.value = "Unexpected error: ${e.message}"
            }
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}
