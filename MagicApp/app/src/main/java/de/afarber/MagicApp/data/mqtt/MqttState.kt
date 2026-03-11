package de.afarber.MagicApp.data.mqtt

enum class MqttConnectionState {
    Disconnected,
    Connecting,
    Connected
}

data class MqttMessageUi(
    val timestamp: String,
    val topic: String,
    val payload: String
)

data class MqttRuntimeState(
    val connectionState: MqttConnectionState = MqttConnectionState.Disconnected,
    val subscribedTopic: String? = null,
    val lastError: String? = null,
    val messages: List<MqttMessageUi> = emptyList()
) {
    val isSubscribed: Boolean
        get() = !subscribedTopic.isNullOrBlank()
}
