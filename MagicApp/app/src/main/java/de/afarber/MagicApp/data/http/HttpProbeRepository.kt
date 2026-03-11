package de.afarber.MagicApp.data.http

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class HttpProbeRepository {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val _state = MutableStateFlow(HttpProbeState())

    val state: StateFlow<HttpProbeState> = _state.asStateFlow()

    suspend fun runRequest(url: String, trustAnyTls: Boolean) {
        val trimmedUrl = url.trim()
        if (trimmedUrl.isBlank()) {
            reportError("URL is empty")
            return
        }

        val timestamp = now()
        _state.update {
            it.copy(
                status = HttpProbeStatus.Running,
                timestamp = timestamp,
                details = null,
                responseCode = null,
                lastError = null
            )
        }

        withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                connection = (URL(trimmedUrl).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 10_000
                    readTimeout = 10_000
                    useCaches = false
                    if (this is HttpsURLConnection && trustAnyTls) {
                        sslSocketFactory = insecureTlsSocketFactory()
                        hostnameVerifier = javax.net.ssl.HostnameVerifier { _, _ -> true }
                    }
                }

                val responseCode = connection.responseCode
                val responseText = (if (responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                })?.bufferedReader()?.use { it.readText() }?.take(240)

                val isSuccess = responseCode in 200..299
                val details = buildString {
                    append("HTTP ").append(responseCode)
                    if (!responseText.isNullOrBlank()) {
                        append(" - ").append(responseText.replace('\n', ' '))
                    }
                }

                if (isSuccess) {
                    Log.i(TAG, "HTTP probe success: $details")
                    _state.update {
                        it.copy(
                            status = HttpProbeStatus.Success,
                            timestamp = now(),
                            responseCode = responseCode,
                            details = details,
                            lastError = null,
                            logLines = listOf("${now()} SUCCESS $details") + it.logLines.take(99)
                        )
                    }
                } else {
                    Log.e(TAG, "HTTP probe failed: $details")
                    _state.update {
                        it.copy(
                            status = HttpProbeStatus.Failure,
                            timestamp = now(),
                            responseCode = responseCode,
                            details = details,
                            lastError = details,
                            logLines = listOf("${now()} FAILURE $details") + it.logLines.take(99)
                        )
                    }
                }
            } catch (e: Exception) {
                val detail = "${e.javaClass.simpleName}: ${e.message ?: "Unknown error"}"
                Log.e(TAG, "HTTP probe exception: $detail", e)
                _state.update {
                    it.copy(
                        status = HttpProbeStatus.Failure,
                        timestamp = now(),
                        responseCode = null,
                        details = detail,
                        lastError = detail,
                        logLines = listOf("${now()} FAILURE $detail") + it.logLines.take(99)
                    )
                }
            } finally {
                connection?.disconnect()
            }
        }
    }

    fun clearOutput() {
        _state.update { current ->
            current.copy(
                details = null,
                lastError = null,
                logLines = emptyList()
            )
        }
    }

    private fun reportError(message: String) {
        Log.e(TAG, message)
        _state.update {
            it.copy(
                status = HttpProbeStatus.Failure,
                timestamp = now(),
                lastError = message,
                details = null,
                logLines = listOf("${now()} FAILURE $message") + it.logLines.take(99)
            )
        }
    }

    private fun insecureTlsSocketFactory() = SSLContext.getInstance("TLS").apply {
        val trustManagers = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = emptyArray()
            }
        )
        init(null, trustManagers, java.security.SecureRandom())
    }.socketFactory

    private fun now(): String = LocalDateTime.now().format(formatter)

    companion object {
        private const val TAG = "MagicApp-HTTP"
    }
}
