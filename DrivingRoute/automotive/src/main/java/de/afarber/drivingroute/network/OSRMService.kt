package de.afarber.drivingroute.network

import de.afarber.drivingroute.model.OSRMResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface OSRMService {
    @GET("route/v1/driving/{coordinates}")
    suspend fun getRoute(
        @Path("coordinates") coordinates: String
    ): Response<OSRMResponse>
}