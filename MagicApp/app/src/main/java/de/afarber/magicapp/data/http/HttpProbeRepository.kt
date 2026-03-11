package de.afarber.magicapp.data.http

import android.util.Log
import de.afarber.magicapp.data.common.ClearableOutput
import de.afarber.magicapp.data.common.StateHolder
import de.afarber.magicapp.data.common.nowTimestamp
import de.afarber.magicapp.data.common.prependCapped
import de.afarber.magicapp.data.common.toErrorText
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class HttpProbeRepository :
    StateHolder<HttpProbeState>,
    ClearableOutput {
    private val _state = MutableStateFlow(HttpProbeState())

    override val state: StateFlow<HttpProbeState> = _state.asStateFlow()

    suspend fun execute(config: HttpProbeConfig) {
        val trimmedUrl = config.url.trim()
        if (trimmedUrl.isEmpty()) {
            reportError("URL is empty")
            return
        }

        setRunningState()

        withContext(Dispatchers.IO) {
            val client =
                KtorHttpClientFactory.create(
                    trustAnyTls = config.trustAnyTls,
                    timeoutMillis = config.timeoutMillis,
                )
            try {
                val response = client.get(trimmedUrl)
                val responseCode = response.status.value
                val responseText = response.bodyAsText().take(240)
                val details =
                    buildString {
                        append("HTTP ").append(responseCode)
                        if (responseText.isNotBlank()) {
                            append(" - ").append(responseText.replace('\n', ' '))
                        }
                    }

                if (responseCode in 200..299) {
                    reportSuccess(responseCode = responseCode, details = details)
                } else {
                    reportFailure(details = details, responseCode = responseCode)
                }
            } catch (e: Exception) {
                reportFailure(details = e.toErrorText(), throwable = e)
            } finally {
                client.close()
            }
        }
    }

    override fun clearOutput() {
        _state.update { current ->
            current.copy(
                details = null,
                lastError = null,
                logLines = emptyList(),
            )
        }
    }

    private fun setRunningState() {
        _state.update {
            it.copy(
                status = HttpProbeStatus.Running,
                timestamp = nowTimestamp(),
                details = null,
                responseCode = null,
                lastError = null,
            )
        }
    }

    private fun reportSuccess(
        responseCode: Int,
        details: String,
    ) {
        Log.i(TAG, "HTTP probe success: $details")
        val timestamp = nowTimestamp()
        _state.update {
            it.copy(
                status = HttpProbeStatus.Success,
                timestamp = timestamp,
                responseCode = responseCode,
                details = details,
                lastError = null,
                logLines = prependCapped("$timestamp SUCCESS $details", it.logLines),
            )
        }
    }

    private fun reportFailure(
        details: String,
        responseCode: Int? = null,
        throwable: Throwable? = null,
    ) {
        if (throwable == null) {
            Log.e(TAG, "HTTP probe failed: $details")
        } else {
            Log.e(TAG, "HTTP probe failed: $details", throwable)
        }

        val timestamp = nowTimestamp()
        _state.update {
            it.copy(
                status = HttpProbeStatus.Failure,
                timestamp = timestamp,
                responseCode = responseCode,
                details = details,
                lastError = details,
                logLines = prependCapped("$timestamp FAILURE $details", it.logLines),
            )
        }
    }

    private fun reportError(message: String) {
        Log.e(TAG, message)
        val timestamp = nowTimestamp()
        _state.update {
            it.copy(
                status = HttpProbeStatus.Failure,
                timestamp = timestamp,
                lastError = message,
                details = null,
                logLines = prependCapped("$timestamp FAILURE $message", it.logLines),
            )
        }
    }

    companion object {
        private const val TAG = "MagicApp-HTTP"
    }
}
