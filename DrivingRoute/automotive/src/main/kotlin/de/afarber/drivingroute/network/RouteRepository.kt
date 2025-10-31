package de.afarber.drivingroute.network

import android.util.Log
import de.afarber.drivingroute.model.OSRMResponse
import de.afarber.openmapview.LatLng
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class RouteRepository {

    private val client = HttpClient(Android) {
        // Base URL configuration
        defaultRequest {
            url("https://router.project-osrm.org/")
        }

        // Content negotiation for JSON serialization
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }

        // Logging plugin
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("KtorClient", message)
                }
            }
            level = LogLevel.BODY
        }

        // Timeout configuration
        install(HttpTimeout) {
            connectTimeoutMillis = 10_000
            requestTimeoutMillis = 10_000
            socketTimeoutMillis = 10_000
        }
    }

    suspend fun getRoute(start: LatLng, finish: LatLng): Result<OSRMResponse> {
        return try {
            val coordinates = "${start.longitude},${start.latitude};${finish.longitude},${finish.latitude}"

            val response = client.get {
                url {
                    path("route/v1/driving/$coordinates")
                }
            }

            // Check if response is successful (2xx status code)
            if (response.status.isSuccess()) {
                val osrmResponse: OSRMResponse = response.body()
                Result.success(osrmResponse)
            } else {
                Result.failure(Exception("API call failed: ${response.status.value} ${response.status.description}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun close() {
        client.close()
    }
}
