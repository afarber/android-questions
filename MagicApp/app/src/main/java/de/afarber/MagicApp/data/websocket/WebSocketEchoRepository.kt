package de.afarber.MagicApp.data.websocket

import android.util.Log
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketException
import com.neovisionaries.ws.client.WebSocketFactory
import com.neovisionaries.ws.client.WebSocketFrame
import de.afarber.MagicApp.data.tls.TrustfulManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WebSocketEchoRepository {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val _state = MutableStateFlow(WebSocketRuntimeState())

    private var socket: WebSocket? = null

    val state: StateFlow<WebSocketRuntimeState> = _state.asStateFlow()

    suspend fun connect(url: String, trustAnyTls: Boolean) {
        val trimmedUrl = url.trim()
        if (trimmedUrl.isBlank()) {
            reportError("WebSocket URL is empty")
            return
        }

        _state.update {
            it.copy(connectionState = WebSocketConnectionState.Connecting, lastError = null)
        }

        withContext(Dispatchers.IO) {
            closeActiveSocket()
            try {
                val factory = WebSocketFactory().setConnectionTimeout(10_000)
                if (trustAnyTls && trimmedUrl.startsWith("wss://", ignoreCase = true)) {
                    factory.setSSLSocketFactory(TrustfulManager.socketFactory())
                    factory.setVerifyHostname(false)
                }

                val newSocket = factory.createSocket(trimmedUrl)
                newSocket.addListener(createListener())
                socket = newSocket
                newSocket.connectAsynchronously()
            } catch (e: Exception) {
                val detail = "Connect failed: ${e.javaClass.simpleName}: ${e.message ?: "Unknown error"}"
                Log.e(TAG, detail, e)
                _state.update {
                    it.copy(
                        connectionState = WebSocketConnectionState.Disconnected,
                        lastError = detail
                    )
                }
            }
        }
    }

    suspend fun reconnect(url: String, trustAnyTls: Boolean) {
        disconnect()
        connect(url = url, trustAnyTls = trustAnyTls)
    }

    suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            closeActiveSocket()
            _state.update {
                it.copy(connectionState = WebSocketConnectionState.Disconnected)
            }
        }
    }

    suspend fun send(payload: String) {
        withContext(Dispatchers.IO) {
            val activeSocket = socket
            if (activeSocket == null || !activeSocket.isOpen) {
                reportError("WebSocket is not connected")
                return@withContext
            }

            try {
                activeSocket.sendText(payload)
                pushMessage(direction = "OUT", payload = payload)
                _state.update { it.copy(lastError = null) }
            } catch (e: Exception) {
                reportError("Send failed: ${e.javaClass.simpleName}: ${e.message ?: "Unknown error"}", e)
            }
        }
    }

    fun clearOutput() {
        _state.update { it.copy(messages = emptyList(), lastError = null) }
    }

    fun close() {
        runCatching { closeActiveSocket() }
        _state.value = WebSocketRuntimeState()
    }

    private fun createListener(): WebSocketAdapter {
        return object : WebSocketAdapter() {
            override fun onConnected(websocket: WebSocket?, headers: MutableMap<String, MutableList<String>>?) {
                Log.i(TAG, "Connected")
                _state.update {
                    it.copy(
                        connectionState = WebSocketConnectionState.Connected,
                        lastError = null
                    )
                }
            }

            override fun onTextMessage(websocket: WebSocket?, text: String?) {
                pushMessage(direction = "IN", payload = text.orEmpty())
            }

            override fun onConnectError(websocket: WebSocket?, exception: WebSocketException?) {
                reportError(
                    "Connect error: ${exception?.javaClass?.simpleName ?: "WebSocketException"}: ${exception?.message ?: "Unknown error"}",
                    exception
                )
                _state.update {
                    it.copy(connectionState = WebSocketConnectionState.Disconnected)
                }
            }

            override fun onDisconnected(
                websocket: WebSocket?,
                serverCloseFrame: WebSocketFrame?,
                clientCloseFrame: WebSocketFrame?,
                closedByServer: Boolean
            ) {
                val detail = serverCloseFrame?.closeReason
                    ?: clientCloseFrame?.closeReason
                    ?: if (closedByServer) "Disconnected by server" else "Disconnected"
                Log.i(TAG, detail)
                _state.update {
                    it.copy(
                        connectionState = WebSocketConnectionState.Disconnected,
                        lastError = if (closedByServer) detail else it.lastError
                    )
                }
            }

            override fun onError(websocket: WebSocket?, cause: WebSocketException?) {
                reportError(
                    "WebSocket error: ${cause?.javaClass?.simpleName ?: "WebSocketException"}: ${cause?.message ?: "Unknown error"}",
                    cause
                )
            }
        }
    }

    private fun pushMessage(direction: String, payload: String) {
        val message = WebSocketMessageUi(
            timestamp = now(),
            direction = direction,
            payload = payload
        )
        _state.update {
            it.copy(messages = listOf(message) + it.messages.take(99))
        }
    }

    private fun reportError(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(TAG, message, throwable)
        } else {
            Log.e(TAG, message)
        }
        _state.update { it.copy(lastError = message) }
    }

    private fun closeActiveSocket() {
        socket?.let { activeSocket ->
            runCatching {
                activeSocket.disconnect()
            }
        }
        socket = null
    }

    private fun now(): String = LocalDateTime.now().format(formatter)

    companion object {
        private const val TAG = "MagicApp-WS"
    }
}
