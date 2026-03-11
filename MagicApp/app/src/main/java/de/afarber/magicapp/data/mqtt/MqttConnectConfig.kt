package de.afarber.magicapp.data.mqtt

data class MqttConnectConfig(
    val serverUri: String,
    val clientId: String,
    val trustAnyTls: Boolean = false,
) {
    companion object {
        fun fromHostPort(
            host: String,
            port: Int,
            clientId: String,
            trustAnyTls: Boolean = false,
        ): MqttConnectConfig {
            val scheme = if (port == 8883) "ssl" else "tcp"
            val serverUri = "$scheme://${host.trim()}:$port"
            return MqttConnectConfig(
                serverUri = serverUri,
                clientId = clientId,
                trustAnyTls = trustAnyTls,
            )
        }
    }
}
