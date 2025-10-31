package de.afarber.drivingroute.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OSRMResponse(
    @SerialName("routes")
    val routes: List<Route>
) {
    @Serializable
    data class Route(
        @SerialName("geometry")
        val geometry: String,
        @SerialName("duration")
        val duration: Double,
        @SerialName("distance")
        val distance: Double
    )
}