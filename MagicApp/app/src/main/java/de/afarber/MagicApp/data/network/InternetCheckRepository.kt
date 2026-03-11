package de.afarber.MagicApp.data.network

import android.util.Log
import de.afarber.MagicApp.data.config.BackendEndpoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InternetCheckRepository {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    suspend fun runCheck(): InternetCheckState = withContext(Dispatchers.IO) {
        val timestamp = LocalDateTime.now().format(formatter)
        var connection: HttpURLConnection? = null

        try {
            connection = (URL(BackendEndpoints.HTTP_URL).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6000
                readTimeout = 6000
                useCaches = false
            }

            val statusCode = connection.responseCode
            if (statusCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
                InternetCheckState(
                    status = InternetStatus.Success,
                    timestamp = timestamp,
                    details = "HTTP $statusCode"
                )
            } else {
                val errorBody = connection.errorStream
                    ?.bufferedReader()
                    ?.use { it.readText() }
                    ?.take(160)
                    ?.ifBlank { null }
                val detail = buildString {
                    append("HTTP ").append(statusCode)
                    if (!errorBody.isNullOrBlank()) {
                        append(" - ").append(errorBody)
                    }
                }
                Log.e(TAG, "Internet check failed: $detail")
                InternetCheckState(
                    status = InternetStatus.Error,
                    timestamp = timestamp,
                    details = detail
                )
            }
        } catch (e: Exception) {
            val detail = "${e.javaClass.simpleName}: ${e.message ?: "Unknown error"}"
            Log.e(TAG, "Internet check exception: $detail", e)
            InternetCheckState(
                status = InternetStatus.Error,
                timestamp = timestamp,
                details = detail
            )
        } finally {
            connection?.disconnect()
        }
    }

    private companion object {
        const val TAG = "MagicApp-HTTP"
    }
}
