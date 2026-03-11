package de.afarber.MagicApp.data.mqtt

import android.util.Log
import de.afarber.MagicApp.data.tls.TrustfulManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class MqttRepository {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val _state = MutableStateFlow(MqttRuntimeState())

    private var client: MqttAsyncClient? = null
    private var reconnectTopic: String? = null

    val state: StateFlow<MqttRuntimeState> = _state.asStateFlow()

    suspend fun connect(
        host: String,
        port: Int,
        clientId: String,
        insecureTls: Boolean = false
    ) {
        if (host.isBlank()) {
            reportError("Broker host is empty")
            return
        }

        if (port <= 0 || port > 65535) {
            reportError("Broker port is invalid")
            return
        }

        _state.update {
            it.copy(connectionState = MqttConnectionState.Connecting, lastError = null)
        }

        withContext(Dispatchers.IO) {
            val serverUris = buildServerUris(host, port)
            var lastFailure: Throwable? = null
            var connected = false
            val attemptErrors = mutableListOf<String>()

            for (serverUri in serverUris) {
                var activeClient: MqttAsyncClient? = null
                try {
                    activeClient = ensureClient(serverUri, clientId)
                    if (activeClient.isConnected) {
                        _state.update {
                            it.copy(connectionState = MqttConnectionState.Connected, lastError = null)
                        }
                        connected = true
                        break
                    }

                    val options = MqttConnectOptions().apply {
                        isAutomaticReconnect = true
                        isCleanSession = true
                        keepAliveInterval = 20
                        connectionTimeout = 10
                        mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1
                        if (insecureTls && serverUri.startsWith("ssl://")) {
                            socketFactory = TrustfulManager.socketFactory()
                        }
                    }

                    Log.i(
                        TAG,
                        "Connecting to $serverUri (insecureTls=${insecureTls && serverUri.startsWith("ssl://")})"
                    )
                    activeClient.connect(options).waitForCompletion(10_000)
                    _state.update {
                        it.copy(connectionState = MqttConnectionState.Connected, lastError = null)
                    }
                    connected = true
                    break
                } catch (e: Exception) {
                    lastFailure = e
                    attemptErrors += "$serverUri -> ${formatError("Attempt failed", e)}"
                    Log.e(TAG, "Connect attempt failed for $serverUri", e)
                    // Ensure partial failed clients/sockets do not linger.
                    activeClient?.let { failedClient ->
                        runCatching {
                            if (failedClient.isConnected) {
                                failedClient.disconnectForcibly(500, 500, false)
                            }
                            failedClient.close()
                        }
                        if (client === failedClient) {
                            client = null
                        }
                    }
                }
            }

            if (!connected) {
                val detail = buildString {
                    append("Connect failed (tried ${serverUris.joinToString()})")
                    if (attemptErrors.isNotEmpty()) {
                        append(": ").append(attemptErrors.joinToString(" | "))
                    } else if (lastFailure != null) {
                        append(": ").append(formatError("Attempt failed", lastFailure))
                    }
                }
                _state.update {
                    it.copy(
                        connectionState = MqttConnectionState.Disconnected,
                        lastError = detail
                    )
                }
            }
        }
    }

    suspend fun disconnect(clearSubscription: Boolean = true) {
        withContext(Dispatchers.IO) {
            try {
                client?.let { activeClient ->
                    if (activeClient.isConnected) {
                        activeClient.disconnect().waitForCompletion(5_000)
                    }
                }
            } catch (_: Exception) {
                // Ignore disconnect errors.
            } finally {
                _state.update {
                    it.copy(
                        connectionState = MqttConnectionState.Disconnected,
                        subscribedTopic = if (clearSubscription) null else it.subscribedTopic
                    )
                }
            }
        }
    }

    suspend fun reconnect(
        host: String,
        port: Int,
        clientId: String,
        insecureTls: Boolean = false
    ) {
        val topicToRestore = _state.value.subscribedTopic
        reconnectTopic = topicToRestore
        disconnect(clearSubscription = false)
        connect(
            host = host,
            port = port,
            clientId = clientId,
            insecureTls = insecureTls
        )
        if (_state.value.connectionState == MqttConnectionState.Connected && !topicToRestore.isNullOrBlank()) {
            subscribe(topicToRestore)
        }
    }

    suspend fun subscribe(topic: String, qos: Int = 0) {
        if (topic.isBlank()) {
            reportError("Topic is empty")
            return
        }

        withContext(Dispatchers.IO) {
            val activeClient = client
            if (activeClient == null || !activeClient.isConnected) {
                reportError("MQTT is not connected")
                return@withContext
            }

            try {
                activeClient.subscribe(topic, qos).waitForCompletion(5_000)
                reconnectTopic = topic
                _state.update {
                    it.copy(subscribedTopic = topic, lastError = null)
                }
            } catch (e: Exception) {
                reportError(formatError("Subscribe failed", e), e)
            }
        }
    }

    suspend fun unsubscribe() {
        val topic = _state.value.subscribedTopic
        if (topic.isNullOrBlank()) {
            return
        }

        withContext(Dispatchers.IO) {
            val activeClient = client
            if (activeClient == null || !activeClient.isConnected) {
                _state.update { it.copy(subscribedTopic = null) }
                reconnectTopic = null
                return@withContext
            }

            try {
                activeClient.unsubscribe(topic).waitForCompletion(5_000)
                reconnectTopic = null
                _state.update {
                    it.copy(subscribedTopic = null, lastError = null)
                }
            } catch (e: Exception) {
                reportError(formatError("Unsubscribe failed", e), e)
            }
        }
    }

    suspend fun publish(topic: String, payload: String, qos: Int = 0, retained: Boolean = false) {
        if (topic.isBlank()) {
            reportError("Topic is empty")
            return
        }

        withContext(Dispatchers.IO) {
            val activeClient = client
            if (activeClient == null || !activeClient.isConnected) {
                reportError("MQTT is not connected")
                return@withContext
            }

            try {
                val message = MqttMessage(payload.toByteArray()).apply {
                    this.qos = qos
                    isRetained = retained
                }
                activeClient.publish(topic, message).waitForCompletion(5_000)
                _state.update { it.copy(lastError = null) }
            } catch (e: Exception) {
                reportError(formatError("Publish failed", e), e)
            }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(messages = emptyList()) }
    }

    fun clearOutput() {
        _state.update { it.copy(messages = emptyList(), lastError = null) }
    }

    fun close() {
        runCatching {
            client?.let { activeClient ->
                if (activeClient.isConnected) {
                    activeClient.disconnectForcibly(500, 500, false)
                }
                activeClient.close()
            }
        }
        client = null
        reconnectTopic = null
        _state.value = MqttRuntimeState()
    }

    private fun ensureClient(serverUri: String, clientId: String): MqttAsyncClient {
        val safeClientId = clientId.ifBlank { generateClientId() }

        val current = client
        if (current != null && current.serverURI == serverUri && current.clientId == safeClientId) {
            return current
        }

        current?.let {
            runCatching {
                if (it.isConnected) {
                    it.disconnectForcibly(500, 500, false)
                }
                it.close()
            }
        }

        return MqttAsyncClient(serverUri, safeClientId, MemoryPersistence()).also { newClient ->
            newClient.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    _state.update {
                        it.copy(connectionState = MqttConnectionState.Connected, lastError = null)
                    }
                    Log.i(TAG, "Connected to ${serverURI.orEmpty()} (reconnect=$reconnect)")
                    val topic = reconnectTopic
                    if (reconnect && !topic.isNullOrBlank()) {
                        runCatching {
                            newClient.subscribe(topic, 0).waitForCompletion(5_000)
                            _state.update { state ->
                                state.copy(subscribedTopic = topic, lastError = null)
                            }
                        }.onFailure {
                            reportError(formatError("Re-subscribe failed", it), it)
                        }
                    }
                }

                override fun connectionLost(cause: Throwable?) {
                    Log.e(TAG, "Connection lost", cause)
                    _state.update {
                        it.copy(
                            connectionState = MqttConnectionState.Disconnected,
                            lastError = cause?.let { throwable ->
                                formatError("Connection lost", throwable)
                            } ?: "Connection lost"
                        )
                    }
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val payload = message?.payload?.toString(Charsets.UTF_8).orEmpty()
                    val item = MqttMessageUi(
                        timestamp = LocalDateTime.now().format(formatter),
                        topic = topic.orEmpty(),
                        payload = payload
                    )
                    _state.update {
                        it.copy(messages = listOf(item) + it.messages.take(99))
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    // Publish confirmation is not shown separately in this UI.
                }
            })
            client = newClient
        }
    }

    private fun reportError(message: String, throwable: Throwable? = null) {
        if (throwable == null) {
            Log.e(TAG, message)
        } else {
            Log.e(TAG, message, throwable)
        }
        _state.update { it.copy(lastError = message) }
    }

    private fun buildServerUris(host: String, port: Int): List<String> {
        return listOf(
            buildUriForHostAndPort(
                host = host,
                port = port
            )
        )
    }

    private fun buildUriForHostAndPort(host: String, port: Int): String {
        return when (port) {
            8883 -> "ssl://$host:$port"
            else -> "tcp://$host:$port"
        }
    }

    private fun formatError(prefix: String, throwable: Throwable): String {
        return if (throwable is MqttException) {
            val base = buildString {
                append(prefix)
                append(" (reasonCode=").append(throwable.reasonCode).append(")")
            }
            val causeText = throwable.cause?.message
            val messageText = throwable.message
            when {
                !causeText.isNullOrBlank() -> "$base: $causeText"
                !messageText.isNullOrBlank() -> "$base: $messageText"
                else -> base
            }
        } else {
            "$prefix: ${throwable.javaClass.simpleName}: ${throwable.message ?: "Unknown error"}"
        }
    }

    companion object {
        private const val TAG = "MagicApp-MQTT"

        fun generateClientId(prefix: String = "MagicApp"): String {
            return "$prefix-${UUID.randomUUID().toString().take(8)}"
        }
    }
}
