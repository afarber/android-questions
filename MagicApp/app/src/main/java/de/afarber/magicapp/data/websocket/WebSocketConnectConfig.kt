package de.afarber.magicapp.data.websocket

data class WebSocketConnectConfig(
    val url: String,
    val trustAnyTls: Boolean,
)
