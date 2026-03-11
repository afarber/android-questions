package de.afarber.magicapp.data.websocket

enum class WebSocketConnectionState {
    Disconnected,
    Connecting,
    Connected,
}

data class WebSocketMessageUi(
    val timestamp: String,
    val direction: String,
    val payload: String,
)

data class WebSocketRuntimeState(
    val connectionState: WebSocketConnectionState = WebSocketConnectionState.Disconnected,
    val lastError: String? = null,
    val messages: List<WebSocketMessageUi> = emptyList(),
)
