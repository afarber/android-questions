package de.afarber.drivingroute.model

import com.google.gson.annotations.SerializedName

data class OSRMResponse(
    @SerializedName("routes")
    val routes: List<Route>
) {
    data class Route(
        @SerializedName("geometry")
        val geometry: String,
        @SerializedName("duration")
        val duration: Double,
        @SerializedName("distance")
        val distance: Double
    )
}