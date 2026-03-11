package de.afarber.magicapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.afarber.magicapp.BuildConfig
import de.afarber.magicapp.data.config.BackendEndpoints
import de.afarber.magicapp.data.connectivity.ConnectivityRepository
import de.afarber.magicapp.data.connectivity.NetworkStateUiModel
import de.afarber.magicapp.data.http.HttpProbeConfig
import de.afarber.magicapp.data.http.HttpProbeRepository
import de.afarber.magicapp.data.http.HttpProbeState
import de.afarber.magicapp.data.http.HttpProbeStatus
import de.afarber.magicapp.data.mqtt.MqttConnectConfig
import de.afarber.magicapp.data.mqtt.MqttConnectionState
import de.afarber.magicapp.data.mqtt.MqttRepository
import de.afarber.magicapp.data.mqtt.MqttRuntimeState
import de.afarber.magicapp.data.network.InternetCheckRepository
import de.afarber.magicapp.data.network.InternetCheckState
import de.afarber.magicapp.data.network.InternetStatus
import de.afarber.magicapp.data.tls.TlsCertificateInfo
import de.afarber.magicapp.data.tls.TlsCertificateInspector
import de.afarber.magicapp.data.websocket.WebSocketConnectConfig
import de.afarber.magicapp.data.websocket.WebSocketConnectionState
import de.afarber.magicapp.data.websocket.WebSocketEchoRepository
import de.afarber.magicapp.data.websocket.WebSocketRuntimeState
import de.afarber.magicapp.ui.components.MagicCard
import de.afarber.magicapp.ui.components.isWideScreen
import de.afarber.magicapp.ui.navigation.MenuSection
import kotlinx.coroutines.launch

private data class ParsedHostPort(
    val host: String,
    val port: Int,
)

private fun parseHostPortFromUrl(
    rawUrl: String,
    defaultPort: Int,
): ParsedHostPort? {
    val endpoint =
        runCatching {
            val normalized = if (rawUrl.contains("://")) rawUrl else "tcp://$rawUrl"
            java.net.URI(normalized)
        }.getOrNull() ?: return null

    val host = endpoint.host ?: return null
    val port = if (endpoint.port > 0) endpoint.port else defaultPort
    return ParsedHostPort(host = host, port = port)
}

@Composable
fun SectionContent(
    selectedSection: MenuSection,
    onInfoClick: () -> Unit,
) {
    when (selectedSection) {
        MenuSection.Connectivity -> connectivitySection(onInfoClick)
        MenuSection.AccountService -> accountServiceSection(onInfoClick)
        MenuSection.ServiceManagement -> serviceManagementSection(onInfoClick)
        MenuSection.HTTP -> httpSection(onInfoClick)
        MenuSection.Websockets -> websocketsSection(onInfoClick)
        MenuSection.MQTT -> mqttSection(onInfoClick)
        MenuSection.MagicService -> magicServiceSection(onInfoClick)
    }
}

@Composable
private fun connectivitySection(onInfoClick: () -> Unit) {
    val context = LocalContext.current
    val connectivityRepository =
        remember(context) {
            ConnectivityRepository(context.applicationContext)
        }
    val internetRepository = remember { InternetCheckRepository() }
    val networkState by connectivityRepository
        .observeNetworkState()
        .collectAsStateWithLifecycle(initialValue = NetworkStateUiModel.unavailable())

    var internetState by remember { mutableStateOf(InternetCheckState.idle()) }
    val scope = rememberCoroutineScope()

    val reloadInternet: () -> Unit = {
        scope.launch {
            internetState = internetRepository.runCheck()
        }
    }

    LaunchedEffect(Unit) {
        reloadInternet()
    }

    if (isWideScreen(1000)) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                networkStateCard(
                    state = networkState,
                    onInfoClick = onInfoClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                internetConnectivityCard(
                    state = internetState,
                    onInfoClick = onInfoClick,
                    onReloadClick = reloadInternet,
                    modifier = Modifier.fillMaxWidth(),
                )
                oemNetworkPreferencesCard(
                    onInfoClick = onInfoClick,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = true),
                )
            }
        }
    } else {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            networkStateCard(state = networkState, onInfoClick = onInfoClick)
            internetConnectivityCard(
                state = internetState,
                onInfoClick = onInfoClick,
                onReloadClick = reloadInternet,
            )
            oemNetworkPreferencesCard(onInfoClick = onInfoClick)
        }
    }
}

@Composable
private fun accountServiceSection(onInfoClick: () -> Unit) {
    if (isWideScreen(1000)) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                getTokenCard(onInfoClick = onInfoClick)
                placeholderInputCard(
                    title = "Token Result History",
                    fieldLabels = listOf("History Filter"),
                    onInfoClick = onInfoClick,
                    onReloadClick = {},
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                placeholderInputCard(
                    title = "Android Accounts",
                    fieldLabels = listOf("Account"),
                    onInfoClick = onInfoClick,
                    onReloadClick = {},
                )
                placeholderInputCard(
                    title = "Token Information",
                    fieldLabels = listOf("Token"),
                    onInfoClick = onInfoClick,
                    onReloadClick = {},
                )
            }
        }
    } else {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            getTokenCard(onInfoClick = onInfoClick)
            placeholderInputCard(
                title = "Token Result History",
                fieldLabels = listOf("History Filter"),
                onInfoClick = onInfoClick,
                onReloadClick = {},
            )
            placeholderInputCard(
                title = "Android Accounts",
                fieldLabels = listOf("Account"),
                onInfoClick = onInfoClick,
                onReloadClick = {},
            )
            placeholderInputCard(
                title = "Token Information",
                fieldLabels = listOf("Token"),
                onInfoClick = onInfoClick,
                onReloadClick = {},
            )
        }
    }
}

@Composable
private fun serviceManagementSection(onInfoClick: () -> Unit) {
    placeholderTwoColumnSection(
        leftTitle = "Service Management Input",
        leftFields = listOf("Service"),
        rightTitle = "Service Management Output",
        rightFields = listOf("Result"),
        onInfoClick = onInfoClick,
    )
}

@Composable
private fun httpSection(onInfoClick: () -> Unit) {
    val repository = remember { HttpProbeRepository() }
    val httpState by repository.state.collectAsStateWithLifecycle()
    val tlsInspector = remember { TlsCertificateInspector() }
    val scope = rememberCoroutineScope()

    var url by rememberSaveable { mutableStateOf(BackendEndpoints.HTTPS_URL) }
    var trustAnyTls by rememberSaveable { mutableStateOf(true) }
    var localError by rememberSaveable { mutableStateOf<String?>(null) }

    var certInfo by remember { mutableStateOf<TlsCertificateInfo?>(null) }
    var certError by rememberSaveable { mutableStateOf<String?>(null) }

    val requestAction: () -> Unit = {
        localError = null
        scope.launch {
            repository.execute(
                HttpProbeConfig(
                    url = url,
                    trustAnyTls = trustAnyTls,
                ),
            )
        }
    }
    val reloadAction: () -> Unit = requestAction

    val showCertAction: () -> Unit = showHttpCert@{
        val parsed = runCatching { java.net.URL(url.trim()) }.getOrNull()
        if (parsed == null || parsed.host.isNullOrBlank()) {
            localError = "Enter a valid URL first"
            return@showHttpCert
        }
        val host = parsed.host
        val port = if (parsed.port > 0) parsed.port else 443
        localError = null
        scope.launch {
            runCatching {
                tlsInspector.fetchCertificateInfo(host = host, port = port, trustAnyTls = trustAnyTls)
            }.onSuccess { info ->
                certInfo = info
                certError = null
            }.onFailure { error ->
                certInfo = null
                certError = "${error.javaClass.simpleName}: ${error.message ?: "Unknown error"}"
            }
        }
    }

    if (certInfo != null || certError != null) {
        backendCertificateDialog(
            info = certInfo,
            error = certError,
            onDismiss = {
                certInfo = null
                certError = null
            },
        )
    }

    transportTwoPane(
        left = {
            httpInputCard(
                url = url,
                onUrlChange = { url = it },
                trustAnyTls = trustAnyTls,
                onTrustAnyTlsChange = { trustAnyTls = it },
                onRequestClick = requestAction,
                onShowCertClick = showCertAction,
                onInfoClick = onInfoClick,
                onReloadClick = reloadAction,
            )
        },
        right = {
            httpOutputCard(
                state = httpState,
                localError = localError,
                onClearClick = {
                    localError = null
                    repository.clearOutput()
                },
                onInfoClick = onInfoClick,
                onReloadClick = reloadAction,
            )
        },
    )
}

@Composable
private fun websocketsSection(onInfoClick: () -> Unit) {
    val repository = remember { WebSocketEchoRepository() }
    val wsState by repository.state.collectAsStateWithLifecycle()
    val tlsInspector = remember { TlsCertificateInspector() }
    val scope = rememberCoroutineScope()

    var wsUrl by rememberSaveable { mutableStateOf(BackendEndpoints.WS_URL) }
    var payload by rememberSaveable { mutableStateOf("") }
    var trustAnyTls by rememberSaveable { mutableStateOf(true) }
    var localError by rememberSaveable { mutableStateOf<String?>(null) }

    var certInfo by remember { mutableStateOf<TlsCertificateInfo?>(null) }
    var certError by rememberSaveable { mutableStateOf<String?>(null) }

    DisposableEffect(repository) {
        onDispose {
            repository.close()
        }
    }

    fun parseHostPortOrReport(): ParsedHostPort? {
        val endpoint = runCatching { java.net.URI(wsUrl.trim()) }.getOrNull()
        if (endpoint == null) {
            localError = "Websocket URL is invalid"
            return null
        }
        val scheme = endpoint.scheme?.lowercase().orEmpty()
        if (scheme !in setOf("http", "https", "ws", "wss")) {
            localError = "Websocket URL must use http/https/ws/wss"
            return null
        }
        val host = endpoint.host
        if (host.isNullOrBlank()) {
            localError = "Websocket URL host is empty"
            return null
        }
        val defaultPort = if (scheme == "http" || scheme == "ws") 80 else 443
        val port = if (endpoint.port > 0) endpoint.port else defaultPort
        return ParsedHostPort(
            host = host,
            port = port,
        ).also {
            localError = null
        }
    }

    val connectAction: () -> Unit = {
        if (wsState.connectionState == WebSocketConnectionState.Connected) {
            scope.launch { repository.disconnect() }
        } else {
            if (wsUrl.isBlank()) {
                localError = "Websocket URL is empty"
            } else {
                localError = null
                scope.launch {
                    repository.connect(
                        WebSocketConnectConfig(
                            url = wsUrl.trim(),
                            trustAnyTls = trustAnyTls,
                        ),
                    )
                }
            }
        }
    }

    val reloadAction: () -> Unit = {
        if (wsUrl.isBlank()) {
            localError = "Websocket URL is empty"
        } else {
            localError = null
            scope.launch {
                repository.reconnect(
                    WebSocketConnectConfig(
                        url = wsUrl.trim(),
                        trustAnyTls = trustAnyTls,
                    ),
                )
            }
        }
    }

    val sendAction: () -> Unit = {
        if (payload.isBlank()) {
            localError = "Payload is empty"
        } else {
            localError = null
            scope.launch { repository.send(payload = payload) }
        }
    }

    val showCertAction: () -> Unit = showWsCert@{
        val endpoint = parseHostPortOrReport() ?: return@showWsCert
        scope.launch {
            runCatching {
                tlsInspector.fetchCertificateInfo(
                    host = endpoint.host,
                    port = endpoint.port,
                    trustAnyTls = trustAnyTls,
                )
            }.onSuccess { info ->
                certInfo = info
                certError = null
            }.onFailure { error ->
                certInfo = null
                certError = "${error.javaClass.simpleName}: ${error.message ?: "Unknown error"}"
            }
        }
    }

    if (certInfo != null || certError != null) {
        backendCertificateDialog(
            info = certInfo,
            error = certError,
            onDismiss = {
                certInfo = null
                certError = null
            },
        )
    }

    transportTwoPane(
        left = {
            webSocketInputCard(
                wsUrl = wsUrl,
                onWsUrlChange = { wsUrl = it },
                payload = payload,
                onPayloadChange = { payload = it },
                wsState = wsState,
                trustAnyTls = trustAnyTls,
                onTrustAnyTlsChange = { trustAnyTls = it },
                onConnectClick = connectAction,
                onSendClick = sendAction,
                onShowCertClick = showCertAction,
                onInfoClick = onInfoClick,
                onReloadClick = reloadAction,
            )
        },
        right = {
            webSocketOutputCard(
                state = wsState,
                localError = localError,
                onClearClick = {
                    localError = null
                    repository.clearOutput()
                },
                onInfoClick = onInfoClick,
                onReloadClick = reloadAction,
            )
        },
    )
}

@Composable
private fun mqttSection(onInfoClick: () -> Unit) {
    val repository = remember { MqttRepository() }
    val mqttState by repository.state.collectAsStateWithLifecycle()
    val tlsInspector = remember { TlsCertificateInspector() }
    val scope = rememberCoroutineScope()
    val mqttEndpointDefaults =
        remember {
            parseHostPortFromUrl(
                rawUrl = BackendEndpoints.MQTT_URL,
                defaultPort = 8883,
            )
        }

    var host by rememberSaveable { mutableStateOf(mqttEndpointDefaults?.host ?: "broker.hivemq.com") }
    var portText by rememberSaveable { mutableStateOf((mqttEndpointDefaults?.port ?: 8883).toString()) }
    var clientId by rememberSaveable { mutableStateOf(MqttRepository.generateClientId()) }
    var topic by rememberSaveable { mutableStateOf("de/afarber/magicapp/test") }
    var payload by rememberSaveable { mutableStateOf("") }
    var trustAnyTls by rememberSaveable { mutableStateOf(true) }
    var localError by rememberSaveable { mutableStateOf<String?>(null) }

    var certInfo by remember { mutableStateOf<TlsCertificateInfo?>(null) }
    var certError by rememberSaveable { mutableStateOf<String?>(null) }

    DisposableEffect(repository) {
        onDispose {
            repository.close()
        }
    }

    fun parsePortOrReport(): Int? {
        val parsed = portText.toIntOrNull()
        return if (parsed == null || parsed <= 0 || parsed > 65535) {
            localError = "Port must be between 1 and 65535"
            null
        } else {
            localError = null
            parsed
        }
    }

    val connectAction: () -> Unit = {
        if (mqttState.connectionState == MqttConnectionState.Connected) {
            scope.launch { repository.disconnect() }
        } else {
            val parsedPort = parsePortOrReport()
            if (parsedPort != null) {
                scope.launch {
                    repository.connect(
                        MqttConnectConfig.fromHostPort(
                            host = host.trim(),
                            port = parsedPort,
                            clientId = clientId.trim(),
                            trustAnyTls = trustAnyTls,
                        ),
                    )
                }
            }
        }
    }

    val reconnectAction: () -> Unit = {
        val parsedPort = parsePortOrReport()
        if (parsedPort != null) {
            scope.launch {
                repository.reconnect(
                    MqttConnectConfig.fromHostPort(
                        host = host.trim(),
                        port = parsedPort,
                        clientId = clientId.trim(),
                        trustAnyTls = trustAnyTls,
                    ),
                )
            }
        }
    }

    val subscribeAction: () -> Unit = {
        if (mqttState.isSubscribed) {
            scope.launch { repository.unsubscribe() }
        } else {
            if (topic.isBlank()) {
                localError = "Topic is empty"
            } else {
                localError = null
                scope.launch { repository.subscribe(topic = topic.trim()) }
            }
        }
    }

    val publishAction: () -> Unit = {
        if (topic.isBlank()) {
            localError = "Topic is empty"
        } else {
            localError = null
            scope.launch {
                repository.publish(
                    topic = topic.trim(),
                    payload = payload,
                )
            }
        }
    }

    val showCertAction: () -> Unit = showMqttCert@{
        val parsedPort = parsePortOrReport() ?: return@showMqttCert
        scope.launch {
            runCatching {
                tlsInspector.fetchCertificateInfo(
                    host = host.trim(),
                    port = parsedPort,
                    trustAnyTls = trustAnyTls,
                )
            }.onSuccess { info ->
                certInfo = info
                certError = null
            }.onFailure { error ->
                certInfo = null
                certError = "${error.javaClass.simpleName}: ${error.message ?: "Unknown error"}"
            }
        }
    }

    if (certInfo != null || certError != null) {
        backendCertificateDialog(
            info = certInfo,
            error = certError,
            onDismiss = {
                certInfo = null
                certError = null
            },
        )
    }

    transportTwoPane(
        left = {
            mqttInputCard(
                title = "MQTT Input",
                host = host,
                onHostChange = { host = it },
                hostLabel = "Broker Host",
                portText = portText,
                onPortChange = { portText = it },
                portLabel = "Broker Port",
                trustAnyTls = trustAnyTls,
                onTrustAnyTlsChange = { trustAnyTls = it },
                clientId = clientId,
                onClientIdChange = { clientId = it },
                topic = topic,
                onTopicChange = { topic = it },
                payload = payload,
                onPayloadChange = { payload = it },
                mqttState = mqttState,
                onConnectClick = connectAction,
                onSubscribeClick = subscribeAction,
                onPublishClick = publishAction,
                onShowCertClick = showCertAction,
                onInfoClick = onInfoClick,
                onReloadClick = reconnectAction,
            )
        },
        right = {
            mqttOutputCard(
                title = "MQTT Output",
                mqttState = mqttState,
                localError = localError,
                onClearMessagesClick = {
                    localError = null
                    repository.clearOutput()
                },
                onInfoClick = onInfoClick,
                onReloadClick = reconnectAction,
            )
        },
    )
}

@Composable
private fun placeholderTwoColumnSection(
    leftTitle: String,
    leftFields: List<String>,
    rightTitle: String,
    rightFields: List<String>,
    onInfoClick: () -> Unit,
) {
    if (isWideScreen(1000)) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            placeholderInputCard(
                title = leftTitle,
                fieldLabels = leftFields,
                onInfoClick = onInfoClick,
                onReloadClick = {},
                modifier = Modifier.weight(1f),
            )
            placeholderInputCard(
                title = rightTitle,
                fieldLabels = rightFields,
                onInfoClick = onInfoClick,
                onReloadClick = {},
                modifier = Modifier.weight(1f),
            )
        }
    } else {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            placeholderInputCard(
                title = leftTitle,
                fieldLabels = leftFields,
                onInfoClick = onInfoClick,
                onReloadClick = {},
            )
            placeholderInputCard(
                title = rightTitle,
                fieldLabels = rightFields,
                onInfoClick = onInfoClick,
                onReloadClick = {},
            )
        }
    }
}

@Composable
private fun transportTwoPane(
    left: @Composable () -> Unit,
    right: @Composable () -> Unit,
) {
    if (isWideScreen(1000)) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(modifier = Modifier.weight(1f)) { left() }
            Box(modifier = Modifier.weight(1f)) { right() }
        }
    } else {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            left()
            right()
        }
    }
}

@Composable
private fun httpInputCard(
    url: String,
    onUrlChange: (String) -> Unit,
    trustAnyTls: Boolean,
    onTrustAnyTlsChange: (Boolean) -> Unit,
    onRequestClick: () -> Unit,
    onShowCertClick: () -> Unit,
    onInfoClick: () -> Unit,
    onReloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MagicCard(
        title = "HTTP Input",
        onInfoClick = onInfoClick,
        onReloadClick = onReloadClick,
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            label = { Text("Request URL") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = trustAnyTls,
                onCheckedChange = onTrustAnyTlsChange,
            )
            Text("Trust any TLS certificate")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onShowCertClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Show Backend TLS Cert")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onRequestClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Request")
        }
    }
}

@Composable
private fun httpOutputCard(
    state: HttpProbeState,
    localError: String?,
    onClearClick: () -> Unit,
    onInfoClick: () -> Unit,
    onReloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusText =
        when (state.status) {
            HttpProbeStatus.Idle -> "Idle"
            HttpProbeStatus.Running -> "Running"
            HttpProbeStatus.Success -> "Success"
            HttpProbeStatus.Failure -> "Failure"
        }

    MagicCard(
        title = "HTTP Output",
        onInfoClick = onInfoClick,
        onReloadClick = onReloadClick,
        modifier = modifier.heightIn(min = 280.dp),
    ) {
        Text("Status: $statusText")
        Text("Timestamp: ${state.timestamp.orEmpty()}")
        Text("Response Code: ${state.responseCode?.toString() ?: "-"}")
        Text("Last Error: ${localError ?: state.lastError ?: "-"}")
        Text("Details: ${state.details ?: "-"}")
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onClearClick,
            modifier = Modifier.align(Alignment.End),
        ) {
            Text("Clear")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp)
                    .verticalScroll(rememberScrollState()),
        ) {
            if (state.logLines.isEmpty()) {
                Text("No messages")
            } else {
                state.logLines.forEach { line ->
                    Text(line)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun webSocketInputCard(
    wsUrl: String,
    onWsUrlChange: (String) -> Unit,
    payload: String,
    onPayloadChange: (String) -> Unit,
    wsState: WebSocketRuntimeState,
    trustAnyTls: Boolean,
    onTrustAnyTlsChange: (Boolean) -> Unit,
    onConnectClick: () -> Unit,
    onSendClick: () -> Unit,
    onShowCertClick: () -> Unit,
    onInfoClick: () -> Unit,
    onReloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val connectLabel =
        if (wsState.connectionState == WebSocketConnectionState.Connected) {
            "Disconnect"
        } else {
            "Connect"
        }

    MagicCard(
        title = "Websockets Input",
        onInfoClick = onInfoClick,
        onReloadClick = onReloadClick,
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = wsUrl,
            onValueChange = onWsUrlChange,
            label = { Text("Websocket URL") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = trustAnyTls,
                onCheckedChange = onTrustAnyTlsChange,
            )
            Text("Trust any TLS certificate")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onShowCertClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Show Backend TLS Cert")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = payload,
            onValueChange = onPayloadChange,
            label = { Text("Payload") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(onClick = onConnectClick, modifier = Modifier.weight(1f)) {
                Text(connectLabel)
            }
            Button(onClick = onSendClick, modifier = Modifier.weight(1f)) {
                Text("Send")
            }
        }
    }
}

@Composable
private fun webSocketOutputCard(
    state: WebSocketRuntimeState,
    localError: String?,
    onClearClick: () -> Unit,
    onInfoClick: () -> Unit,
    onReloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusText =
        when (state.connectionState) {
            WebSocketConnectionState.Disconnected -> "Disconnected"
            WebSocketConnectionState.Connecting -> "Connecting"
            WebSocketConnectionState.Connected -> "Connected"
        }

    MagicCard(
        title = "Websockets Output",
        onInfoClick = onInfoClick,
        onReloadClick = onReloadClick,
        modifier = modifier.heightIn(min = 280.dp),
    ) {
        Text("Status: $statusText")
        Text("Last Error: ${localError ?: state.lastError ?: "-"}")
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onClearClick,
            modifier = Modifier.align(Alignment.End),
        ) {
            Text("Clear")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp)
                    .verticalScroll(rememberScrollState()),
        ) {
            if (state.messages.isEmpty()) {
                Text("No messages")
            } else {
                state.messages.forEach { message ->
                    Text("${message.timestamp} ${message.direction}")
                    Text(message.payload)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun backendCertificateDialog(
    info: TlsCertificateInfo?,
    error: String?,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            if (info != null) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Subject: ${info.subject}")
                    Text("Issuer: ${info.issuer}")
                    Text("Not Before: ${info.notBefore}")
                    Text("Not After: ${info.notAfter}")
                }
            } else {
                Text("TLS certificate lookup failed: ${error ?: "Unknown error"}")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
    )
}

@Composable
private fun mqttInputCard(
    title: String,
    host: String,
    onHostChange: (String) -> Unit,
    hostLabel: String,
    portText: String,
    onPortChange: (String) -> Unit,
    portLabel: String,
    trustAnyTls: Boolean,
    onTrustAnyTlsChange: (Boolean) -> Unit,
    clientId: String,
    onClientIdChange: (String) -> Unit,
    topic: String,
    onTopicChange: (String) -> Unit,
    payload: String,
    onPayloadChange: (String) -> Unit,
    mqttState: MqttRuntimeState,
    onConnectClick: () -> Unit,
    onSubscribeClick: () -> Unit,
    onPublishClick: () -> Unit,
    onShowCertClick: () -> Unit,
    onInfoClick: () -> Unit,
    onReloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val connectLabel =
        if (mqttState.connectionState == MqttConnectionState.Connected) {
            "Disconnect"
        } else {
            "Connect"
        }
    val subscribeLabel = if (mqttState.isSubscribed) "Unsubscribe" else "Subscribe"

    MagicCard(
        title = title,
        onInfoClick = onInfoClick,
        onReloadClick = onReloadClick,
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = host,
            onValueChange = onHostChange,
            label = { Text(hostLabel) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = portText,
            onValueChange = onPortChange,
            label = { Text(portLabel) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = trustAnyTls,
                onCheckedChange = onTrustAnyTlsChange,
            )
            Text("Trust any TLS certificate")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onShowCertClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Show Backend TLS Cert")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = clientId,
            onValueChange = onClientIdChange,
            label = { Text("Client ID") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = topic,
            onValueChange = onTopicChange,
            label = { Text("Topic") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = payload,
            onValueChange = onPayloadChange,
            label = { Text("Payload") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(onClick = onConnectClick, modifier = Modifier.weight(1f)) {
                Text(connectLabel)
            }
            Button(onClick = onSubscribeClick, modifier = Modifier.weight(1f)) {
                Text(subscribeLabel)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onPublishClick,
            modifier =
                Modifier
                    .fillMaxWidth(),
        ) {
            Text("Publish")
        }
    }
}

@Composable
private fun mqttOutputCard(
    title: String,
    mqttState: MqttRuntimeState,
    localError: String?,
    onClearMessagesClick: () -> Unit,
    onInfoClick: () -> Unit,
    onReloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusText =
        when (mqttState.connectionState) {
            MqttConnectionState.Disconnected -> "Disconnected"
            MqttConnectionState.Connecting -> "Connecting"
            MqttConnectionState.Connected -> "Connected"
        }

    MagicCard(
        title = title,
        onInfoClick = onInfoClick,
        onReloadClick = onReloadClick,
        modifier = modifier.heightIn(min = 280.dp),
    ) {
        Text("Status: $statusText")
        Text("Subscribed Topic: ${mqttState.subscribedTopic.orEmpty()}")
        Text("Last Error: ${localError ?: mqttState.lastError ?: "-"}")
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onClearMessagesClick,
            modifier = Modifier.align(Alignment.End),
        ) {
            Text("Clear")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp)
                    .verticalScroll(rememberScrollState()),
        ) {
            if (mqttState.messages.isEmpty()) {
                Text("No messages")
            } else {
                mqttState.messages.forEach { message ->
                    Text("${message.timestamp} ${message.topic}")
                    Text(message.payload)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun magicServiceSection(onInfoClick: () -> Unit) {
    val context = LocalContext.current
    val appVersion = BuildConfig.VERSION_NAME
    val packageName = context.packageName

    if (isWideScreen(1000)) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            magicServiceInformationCard(
                appVersion = appVersion,
                packageName = packageName,
                onInfoClick = onInfoClick,
                modifier = Modifier.weight(1f),
            )
            invocationUrlsCard(
                onInfoClick = onInfoClick,
                modifier = Modifier.weight(1f),
            )
        }
    } else {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            magicServiceInformationCard(
                appVersion = appVersion,
                packageName = packageName,
                onInfoClick = onInfoClick,
            )
            invocationUrlsCard(onInfoClick = onInfoClick)
        }
    }
}

@Composable
private fun networkStateCard(
    state: NetworkStateUiModel,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MagicCard(
        title = "Network State",
        onInfoClick = onInfoClick,
        modifier = modifier.heightIn(min = 280.dp),
    ) {
        val boolText: (Boolean?) -> String = {
            when (it) {
                true -> "true"
                false -> "false"
                null -> "n/a"
            }
        }

        if (!state.available) {
            Text("No active network")
            return@MagicCard
        }

        Text("Network ID: ${state.networkId}")
        Text("Transports: ${state.transports}")
        Text("Capability VALIDATED: ${boolText(state.validated)}")
        Text("Capability OEM_PAID: ${boolText(state.oemPaid)}")
        Text("Capability OEM_PRIVATE: ${boolText(state.oemPrivate)}")
        Text("Interface Name: ${state.interfaceName}")
        Text("DNS Addresses: ${state.dnsAddresses}")
        Text("Capabilities:")
        Text(state.capabilities)
    }
}

@Composable
private fun internetConnectivityCard(
    state: InternetCheckState,
    onInfoClick: () -> Unit,
    onReloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val timestamp = state.timestamp?.let { " ($it)" }.orEmpty()
    val display =
        when (state.status) {
            InternetStatus.Success -> "Success$timestamp"
            InternetStatus.Error -> "Failure$timestamp"
            InternetStatus.Idle -> "Not checked"
        }

    MagicCard(
        title = "Internet Connectivity",
        onInfoClick = onInfoClick,
        onReloadClick = onReloadClick,
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = display,
            onValueChange = {},
            readOnly = true,
            label = { Text("HTTP request status") },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun oemNetworkPreferencesCard(
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MagicCard(
        title = "OEM Network Preferences",
        onInfoClick = onInfoClick,
        modifier = modifier.heightIn(min = 260.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(180.dp),
        )
    }
}

@Composable
private fun getTokenCard(
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var accountName by rememberSaveable { mutableStateOf("") }
    var operationTypeId by rememberSaveable { mutableStateOf("") }

    MagicCard(
        title = "Get Token",
        onInfoClick = onInfoClick,
        onReloadClick = {},
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = accountName,
            onValueChange = { accountName = it },
            label = { Text("Account Name") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = operationTypeId,
            onValueChange = { operationTypeId = it },
            label = { Text("OperationTypeId") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {}, modifier = Modifier.align(Alignment.End)) {
            Text("Retrieve")
        }
    }
}

@Composable
private fun placeholderInputCard(
    title: String,
    fieldLabels: List<String>,
    onInfoClick: () -> Unit,
    onReloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var values by rememberSaveable(title) { mutableStateOf(fieldLabels.associateWith { "" }) }

    MagicCard(
        title = title,
        onInfoClick = onInfoClick,
        onReloadClick = onReloadClick,
        modifier = modifier,
    ) {
        fieldLabels.forEachIndexed { index, label ->
            OutlinedTextField(
                value = values[label].orEmpty(),
                onValueChange = { text -> values = values.toMutableMap().apply { put(label, text) } },
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth(),
            )
            if (index != fieldLabels.lastIndex) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun magicServiceInformationCard(
    appVersion: String,
    packageName: String,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var status by rememberSaveable { mutableStateOf("connected") }

    MagicCard(
        title = "Information",
        onInfoClick = onInfoClick,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = status,
                onValueChange = { status = it },
                label = { Text("Status") },
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = {}) {
                Text("close")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text("MAGIC Service Information:", fontWeight = FontWeight.SemiBold)
        Text("Version: $appVersion")
        Text("Package: $packageName")
        Text("UID: ")
        Spacer(modifier = Modifier.height(8.dp))
        Text("MEM MetaData Information:", fontWeight = FontWeight.SemiBold)
        Text("ESO FW Version: ")
        Text("Magic Client Version: ")
        Text("OSAM Version: ")
        Text("SCM Version: ")
    }
}

@Composable
private fun invocationUrlsCard(
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var registerPairing by rememberSaveable { mutableStateOf("") }
    var mqttBackend by rememberSaveable { mutableStateOf("") }
    var tokenServer by rememberSaveable { mutableStateOf("") }

    MagicCard(
        title = "Invocation URLs",
        onInfoClick = onInfoClick,
        onReloadClick = {},
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = registerPairing,
            onValueChange = { registerPairing = it },
            label = { Text("Register & Pairing") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = mqttBackend,
            onValueChange = { mqttBackend = it },
            label = { Text("MQTT Backend Broker") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = tokenServer,
            onValueChange = { tokenServer = it },
            label = { Text("Token Management Authentication Server") },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
