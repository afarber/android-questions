package de.afarber.magicapp.data.network

import android.util.Log
import de.afarber.magicapp.data.config.BackendEndpoints
import de.afarber.magicapp.data.http.KtorHttpClientFactory
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InternetCheckRepository {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    suspend fun runCheck(): InternetCheckState =
        withContext(Dispatchers.IO) {
            val timestamp = LocalDateTime.now().format(formatter)
            val client =
                KtorHttpClientFactory.create(
                    trustAnyTls = false,
                    timeoutMillis = 6_000L,
                )

            try {
                val response = client.get(BackendEndpoints.HTTP_URL)
                val statusCode = response.status.value
                if (statusCode in 200..299) {
                    InternetCheckState(
                        status = InternetStatus.Success,
                        timestamp = timestamp,
                        details = "HTTP $statusCode",
                    )
                } else {
                    val detail = "HTTP $statusCode"
                    Log.e(TAG, "Internet check failed: $detail")
                    InternetCheckState(
                        status = InternetStatus.Error,
                        timestamp = timestamp,
                        details = detail,
                    )
                }
            } catch (e: Exception) {
                val detail = "${e.javaClass.simpleName}: ${e.message ?: "Unknown error"}"
                Log.e(TAG, "Internet check exception: $detail", e)
                InternetCheckState(
                    status = InternetStatus.Error,
                    timestamp = timestamp,
                    details = detail,
                )
            } finally {
                client.close()
            }
        }

    private companion object {
        const val TAG = "MagicApp-HTTP"
    }
}
