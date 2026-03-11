package de.afarber.magicapp.data.websocket

import android.util.Log
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketException
import com.neovisionaries.ws.client.WebSocketFactory
import com.neovisionaries.ws.client.WebSocketFrame
import de.afarber.magicapp.data.common.ClearableOutput
import de.afarber.magicapp.data.common.ClosableRepo
import de.afarber.magicapp.data.common.ConnectableRepo
import de.afarber.magicapp.data.common.StateHolder
import de.afarber.magicapp.data.common.nowTimestamp
import de.afarber.magicapp.data.common.prependCapped
import de.afarber.magicapp.data.common.toErrorText
import de.afarber.magicapp.data.tls.TrustfulManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class WebSocketEchoRepository :
    StateHolder<WebSocketRuntimeState>,
    ClearableOutput,
    ClosableRepo,
    ConnectableRepo<WebSocketConnectConfig> {
    private val _state = MutableStateFlow(WebSocketRuntimeState())

    private var socket: WebSocket? = null

    override val state: StateFlow<WebSocketRuntimeState> = _state.asStateFlow()

    override suspend fun connect(config: WebSocketConnectConfig) {
        val trimmedUrl = config.url.trim()
        if (trimmedUrl.isBlank()) {
            reportError("WebSocket URL is empty")
            return
        }

        _state.update {
            it.copy(
                connectionState = WebSocketConnectionState.Connecting,
                lastError = null,
            )
        }

        withContext(Dispatchers.IO) {
            closeActiveSocket()
            try {
                val factory = WebSocketFactory().setConnectionTimeout(10_000)
                if (config.trustAnyTls && trimmedUrl.startsWith("wss://", ignoreCase = true)) {
                    factory.setSSLSocketFactory(TrustfulManager.socketFactory())
                    factory.setVerifyHostname(false)
                }

                val newSocket = factory.createSocket(trimmedUrl)
                newSocket.addListener(createListener())
                socket = newSocket
                newSocket.connectAsynchronously()
            } catch (e: Exception) {
                val detail = "Connect failed: ${e.toErrorText()}"
                Log.e(TAG, detail, e)
                _state.update {
                    it.copy(
                        connectionState = WebSocketConnectionState.Disconnected,
                        lastError = detail,
                    )
                }
            }
        }
    }

    override suspend fun disconnect() {
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
                reportError("Send failed: ${e.toErrorText()}", e)
            }
        }
    }

    override fun clearOutput() {
        _state.update { it.copy(messages = emptyList(), lastError = null) }
    }

    override fun close() {
        runCatching { closeActiveSocket() }
        _state.value = WebSocketRuntimeState()
    }

    private fun createListener(): WebSocketAdapter =
        object : WebSocketAdapter() {
            override fun onConnected(
                websocket: WebSocket?,
                headers: MutableMap<String, MutableList<String>>?,
            ) {
                Log.i(TAG, "Connected")
                _state.update {
                    it.copy(
                        connectionState = WebSocketConnectionState.Connected,
                        lastError = null,
                    )
                }
            }

            override fun onTextMessage(
                websocket: WebSocket?,
                text: String?,
            ) {
                pushMessage(direction = "IN", payload = text.orEmpty())
            }

            override fun onConnectError(
                websocket: WebSocket?,
                exception: WebSocketException?,
            ) {
                reportError(
                    "Connect error: ${exception?.toErrorText() ?: "WebSocketException: Unknown error"}",
                    exception,
                )
                _state.update {
                    it.copy(connectionState = WebSocketConnectionState.Disconnected)
                }
            }

            override fun onDisconnected(
                websocket: WebSocket?,
                serverCloseFrame: WebSocketFrame?,
                clientCloseFrame: WebSocketFrame?,
                closedByServer: Boolean,
            ) {
                val detail =
                    serverCloseFrame?.closeReason
                        ?: clientCloseFrame?.closeReason
                        ?: if (closedByServer) "Disconnected by server" else "Disconnected"
                Log.i(TAG, detail)
                _state.update {
                    it.copy(
                        connectionState = WebSocketConnectionState.Disconnected,
                        lastError = if (closedByServer) detail else it.lastError,
                    )
                }
            }

            override fun onError(
                websocket: WebSocket?,
                cause: WebSocketException?,
            ) {
                reportError(
                    "WebSocket error: ${cause?.toErrorText() ?: "WebSocketException: Unknown error"}",
                    cause,
                )
            }
        }

    private fun pushMessage(
        direction: String,
        payload: String,
    ) {
        val message =
            WebSocketMessageUi(
                timestamp = nowTimestamp(),
                direction = direction,
                payload = payload,
            )
        _state.update {
            it.copy(messages = prependCapped(message, it.messages))
        }
    }

    private fun reportError(
        message: String,
        throwable: Throwable? = null,
    ) {
        if (throwable == null) {
            Log.e(TAG, message)
        } else {
            Log.e(TAG, message, throwable)
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

    companion object {
        private const val TAG = "MagicApp-WS"
    }
}
