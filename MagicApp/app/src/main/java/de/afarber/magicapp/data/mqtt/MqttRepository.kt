package de.afarber.magicapp.data.mqtt

import android.util.Log
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
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.net.URI
import java.util.UUID

class MqttRepository :
    StateHolder<MqttRuntimeState>,
    ClearableOutput,
    ClosableRepo,
    ConnectableRepo<MqttConnectConfig> {
    private val _state = MutableStateFlow(MqttRuntimeState())

    private var client: MqttAsyncClient? = null

    override val state: StateFlow<MqttRuntimeState> = _state.asStateFlow()

    override suspend fun connect(config: MqttConnectConfig) {
        val serverUri = config.serverUri.trim()
        if (!validateConnectConfig(config.copy(serverUri = serverUri))) {
            return
        }

        _state.update {
            it.copy(
                connectionState = MqttConnectionState.Connecting,
                lastError = null,
            )
        }

        withContext(Dispatchers.IO) {
            var activeClient: MqttAsyncClient? = null
            try {
                activeClient = ensureClient(serverUri = serverUri, clientId = config.clientId)
                if (activeClient.isConnected) {
                    _state.update {
                        it.copy(
                            connectionState = MqttConnectionState.Connected,
                            lastError = null,
                        )
                    }
                    return@withContext
                }

                val options = buildConnectOptions(serverUri = serverUri, trustAnyTls = config.trustAnyTls)
                Log.i(
                    TAG,
                    "Connecting to $serverUri (trustAnyTls=${config.trustAnyTls && serverUri.startsWith("ssl://", ignoreCase = true)})",
                )
                activeClient.connect(options).waitForCompletion(10_000)
                _state.update {
                    it.copy(
                        connectionState = MqttConnectionState.Connected,
                        lastError = null,
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Connect failed for $serverUri", e)
                activeClient?.let { closeClient(it) }
                if (client === activeClient) {
                    client = null
                }
                _state.update {
                    it.copy(
                        connectionState = MqttConnectionState.Disconnected,
                        lastError = formatMqttError("Connect failed", e),
                    )
                }
            }
        }
    }

    override suspend fun disconnect() {
        disconnectInternal(clearSubscription = true)
    }

    override suspend fun reconnect(config: MqttConnectConfig) {
        val topicToRestore = _state.value.subscribedTopic
        disconnectInternal(clearSubscription = false)
        connect(config)
        if (_state.value.connectionState == MqttConnectionState.Connected && !topicToRestore.isNullOrBlank()) {
            subscribe(topic = topicToRestore)
        }
    }

    suspend fun subscribe(
        topic: String,
        qos: Int = 0,
    ) {
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
                _state.update {
                    it.copy(subscribedTopic = topic, lastError = null)
                }
            } catch (e: Exception) {
                reportError(formatMqttError("Subscribe failed", e), e)
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
                return@withContext
            }

            try {
                activeClient.unsubscribe(topic).waitForCompletion(5_000)
                _state.update {
                    it.copy(subscribedTopic = null, lastError = null)
                }
            } catch (e: Exception) {
                reportError(formatMqttError("Unsubscribe failed", e), e)
            }
        }
    }

    suspend fun publish(
        topic: String,
        payload: String,
        qos: Int = 0,
        retained: Boolean = false,
    ) {
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
                val message =
                    MqttMessage(payload.toByteArray()).apply {
                        this.qos = qos
                        isRetained = retained
                    }
                activeClient.publish(topic, message).waitForCompletion(5_000)
                _state.update { it.copy(lastError = null) }
            } catch (e: Exception) {
                reportError(formatMqttError("Publish failed", e), e)
            }
        }
    }

    override fun clearOutput() {
        _state.update { it.copy(messages = emptyList(), lastError = null) }
    }

    override fun close() {
        runCatching { client?.let { closeClient(it) } }
        client = null
        _state.value = MqttRuntimeState()
    }

    private suspend fun disconnectInternal(clearSubscription: Boolean) {
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
                        subscribedTopic = if (clearSubscription) null else it.subscribedTopic,
                    )
                }
            }
        }
    }

    private fun validateConnectConfig(config: MqttConnectConfig): Boolean {
        if (config.serverUri.isBlank()) {
            reportError("Server URI is empty")
            return false
        }

        val uri = runCatching { URI(config.serverUri) }.getOrNull()
        if (uri == null || uri.host.isNullOrBlank() || uri.port !in 1..65535) {
            reportError("Server URI is invalid")
            return false
        }
        if (uri.scheme !in setOf("tcp", "ssl")) {
            reportError("Server URI must use tcp:// or ssl://")
            return false
        }
        return true
    }

    private fun ensureClient(
        serverUri: String,
        clientId: String,
    ): MqttAsyncClient {
        val safeClientId = clientId.ifBlank { generateClientId() }
        val current = client
        if (current != null && current.serverURI == serverUri && current.clientId == safeClientId) {
            return current
        }

        current?.let { closeClient(it) }

        val newClient = MqttAsyncClient(serverUri, safeClientId, MemoryPersistence())
        newClient.setCallback(createCallback())
        client = newClient
        return newClient
    }

    private fun createCallback(): MqttCallbackExtended =
        object : MqttCallbackExtended {
            override fun connectComplete(
                reconnect: Boolean,
                serverURI: String?,
            ) {
                _state.update {
                    it.copy(
                        connectionState = MqttConnectionState.Connected,
                        lastError = null,
                    )
                }
                Log.i(TAG, "Connected to ${serverURI.orEmpty()} (reconnect=$reconnect)")
            }

            override fun connectionLost(cause: Throwable?) {
                Log.e(TAG, "Connection lost", cause)
                _state.update {
                    it.copy(
                        connectionState = MqttConnectionState.Disconnected,
                        lastError =
                            cause?.let { throwable ->
                                formatMqttError("Connection lost", throwable)
                            } ?: "Connection lost",
                    )
                }
            }

            override fun messageArrived(
                topic: String?,
                message: MqttMessage?,
            ) {
                val item =
                    MqttMessageUi(
                        timestamp = nowTimestamp(),
                        topic = topic.orEmpty(),
                        payload = message?.payload?.toString(Charsets.UTF_8).orEmpty(),
                    )
                _state.update {
                    it.copy(messages = prependCapped(item, it.messages))
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                // Publish confirmation is not shown separately in this UI.
            }
        }

    private fun buildConnectOptions(
        serverUri: String,
        trustAnyTls: Boolean,
    ): MqttConnectOptions =
        MqttConnectOptions().apply {
            isAutomaticReconnect = false
            isCleanSession = true
            keepAliveInterval = 20
            connectionTimeout = 10
            mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1
            if (trustAnyTls && serverUri.startsWith("ssl://", ignoreCase = true)) {
                socketFactory = TrustfulManager.socketFactory()
            }
        }

    private fun closeClient(activeClient: MqttAsyncClient) {
        runCatching {
            if (activeClient.isConnected) {
                activeClient.disconnectForcibly(500, 500, false)
            }
            activeClient.close()
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

    private fun formatMqttError(
        prefix: String,
        throwable: Throwable,
    ): String =
        if (throwable is MqttException) {
            val base = "$prefix (reasonCode=${throwable.reasonCode})"
            val causeText = throwable.cause?.message
            val messageText = throwable.message
            when {
                !causeText.isNullOrBlank() -> "$base: $causeText"
                !messageText.isNullOrBlank() -> "$base: $messageText"
                else -> base
            }
        } else {
            "$prefix: ${throwable.toErrorText()}"
        }

    companion object {
        private const val TAG = "MagicApp-MQTT"

        fun generateClientId(prefix: String = "MagicApp"): String = "$prefix-${UUID.randomUUID().toString().take(8)}"
    }
}
