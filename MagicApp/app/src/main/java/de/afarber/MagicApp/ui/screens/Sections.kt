package de.afarber.MagicApp.ui.screens

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
import de.afarber.MagicApp.BuildConfig
import de.afarber.MagicApp.data.connectivity.ConnectivityRepository
import de.afarber.MagicApp.data.connectivity.NetworkStateUiModel
import de.afarber.MagicApp.data.mqtt.MqttConnectionState
import de.afarber.MagicApp.data.mqtt.MqttRepository
import de.afarber.MagicApp.data.mqtt.MqttRuntimeState
import de.afarber.MagicApp.data.network.InternetCheckRepository
import de.afarber.MagicApp.data.network.InternetCheckState
import de.afarber.MagicApp.data.network.InternetStatus
import de.afarber.MagicApp.ui.components.MagicCard
import de.afarber.MagicApp.ui.components.isWideScreen
import de.afarber.MagicApp.ui.navigation.MenuSection
import kotlinx.coroutines.launch

@Composable
fun SectionContent(selectedSection: MenuSection, onInfoClick: () -> Unit) {
    when (selectedSection) {
        MenuSection.Connectivity -> ConnectivitySection(onInfoClick)
        MenuSection.AccountService -> AccountServiceSection(onInfoClick)
        MenuSection.ServiceManagement -> ServiceManagementSection(onInfoClick)
        MenuSection.MQTT -> MqttSection(onInfoClick)
        MenuSection.MagicService -> MagicServiceSection(onInfoClick)
    }
}

@Composable
private fun ConnectivitySection(onInfoClick: () -> Unit) {
    val context = LocalContext.current
    val connectivityRepository = remember(context) {
        ConnectivityRepository(context.applicationContext)
    }
    val internetRepository = remember { InternetCheckRepository() }
    val networkState by connectivityRepository.observeNetworkState()
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
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                NetworkStateCard(
                    state = networkState,
                    onInfoClick = onInfoClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InternetConnectivityCard(
                    state = internetState,
                    onInfoClick = onInfoClick,
                    onReloadClick = reloadInternet,
                    modifier = Modifier.fillMaxWidth()
                )
                OemNetworkPreferencesCard(
                    onInfoClick = onInfoClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = true)
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NetworkStateCard(state = networkState, onInfoClick = onInfoClick)
            InternetConnectivityCard(
                state = internetState,
                onInfoClick = onInfoClick,
                onReloadClick = reloadInternet
            )
            OemNetworkPreferencesCard(onInfoClick = onInfoClick)
        }
    }
}

@Composable
private fun AccountServiceSection(onInfoClick: () -> Unit) {
    if (isWideScreen(1000)) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GetTokenCard(onInfoClick = onInfoClick)
                PlaceholderInputCard(
                    title = "Token Result History",
                    fieldLabels = listOf("History Filter"),
                    onInfoClick = onInfoClick,
                    onReloadClick = {}
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PlaceholderInputCard(
                    title = "Android Accounts",
                    fieldLabels = listOf("Account"),
                    onInfoClick = onInfoClick,
                    onReloadClick = {}
                )
                PlaceholderInputCard(
                    title = "Token Information",
                    fieldLabels = listOf("Token"),
                    onInfoClick = onInfoClick,
                    onReloadClick = {}
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GetTokenCard(onInfoClick = onInfoClick)
            PlaceholderInputCard(
                title = "Token Result History",
                fieldLabels = listOf("History Filter"),
                onInfoClick = onInfoClick,
                onReloadClick = {}
            )
            PlaceholderInputCard(
                title = "Android Accounts",
                fieldLabels = listOf("Account"),
                onInfoClick = onInfoClick,
                onReloadClick = {}
            )
            PlaceholderInputCard(
                title = "Token Information",
                fieldLabels = listOf("Token"),
                onInfoClick = onInfoClick,
                onReloadClick = {}
            )
        }
    }
}

@Composable
private fun ServiceManagementSection(onInfoClick: () -> Unit) {
    PlaceholderTwoColumnSection(
        leftTitle = "Service Management Input",
        leftFields = listOf("Service"),
        rightTitle = "Service Management Output",
        rightFields = listOf("Result"),
        onInfoClick = onInfoClick
    )
}

@Composable
private fun MqttSection(onInfoClick: () -> Unit) {
    val repository = remember { MqttRepository() }
    val mqttState by repository.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var host by rememberSaveable { mutableStateOf("broker.hivemq.com") }
    var portText by rememberSaveable { mutableStateOf("1883") }
    var clientId by rememberSaveable { mutableStateOf(MqttRepository.generateClientId()) }
    var topic by rememberSaveable { mutableStateOf("de/afarber/magicapp/test") }
    var payload by rememberSaveable { mutableStateOf("") }
    var insecureTls by rememberSaveable { mutableStateOf(false) }
    var localError by rememberSaveable { mutableStateOf<String?>(null) }

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
                        host = host.trim(),
                        port = parsedPort,
                        clientId = clientId.trim(),
                        insecureTls = insecureTls
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
                    host = host.trim(),
                    port = parsedPort,
                    clientId = clientId.trim(),
                    insecureTls = insecureTls
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
                    payload = payload
                )
            }
        }
    }

    if (isWideScreen(1000)) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MqttInputCard(
                host = host,
                onHostChange = { host = it },
                portText = portText,
                onPortChange = { portText = it },
                insecureTls = insecureTls,
                onInsecureTlsChange = { insecureTls = it },
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
                onInfoClick = onInfoClick,
                onReloadClick = reconnectAction,
                modifier = Modifier.weight(1f)
            )
            MqttOutputCard(
                mqttState = mqttState,
                localError = localError,
                onClearMessagesClick = {
                    localError = null
                    repository.clearOutput()
                },
                onInfoClick = onInfoClick,
                onReloadClick = reconnectAction,
                modifier = Modifier.weight(1f)
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MqttInputCard(
                host = host,
                onHostChange = { host = it },
                portText = portText,
                onPortChange = { portText = it },
                insecureTls = insecureTls,
                onInsecureTlsChange = { insecureTls = it },
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
                onInfoClick = onInfoClick,
                onReloadClick = reconnectAction
            )
            MqttOutputCard(
                mqttState = mqttState,
                localError = localError,
                onClearMessagesClick = {
                    localError = null
                    repository.clearOutput()
                },
                onInfoClick = onInfoClick,
                onReloadClick = reconnectAction
            )
        }
    }
}

@Composable
private fun PlaceholderTwoColumnSection(
    leftTitle: String,
    leftFields: List<String>,
    rightTitle: String,
    rightFields: List<String>,
    onInfoClick: () -> Unit
) {
    if (isWideScreen(1000)) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PlaceholderInputCard(
                title = leftTitle,
                fieldLabels = leftFields,
                onInfoClick = onInfoClick,
                onReloadClick = {},
                modifier = Modifier.weight(1f)
            )
            PlaceholderInputCard(
                title = rightTitle,
                fieldLabels = rightFields,
                onInfoClick = onInfoClick,
                onReloadClick = {},
                modifier = Modifier.weight(1f)
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PlaceholderInputCard(
                title = leftTitle,
                fieldLabels = leftFields,
                onInfoClick = onInfoClick,
                onReloadClick = {}
            )
            PlaceholderInputCard(
                title = rightTitle,
                fieldLabels = rightFields,
                onInfoClick = onInfoClick,
                onReloadClick = {}
            )
        }
    }
}

@Composable
private fun MqttInputCard(
    host: String,
    onHostChange: (String) -> Unit,
    portText: String,
    onPortChange: (String) -> Unit,
    insecureTls: Boolean,
    onInsecureTlsChange: (Boolean) -> Unit,
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
    onInfoClick: () -> Unit,
    onReloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val connectLabel = if (mqttState.connectionState == MqttConnectionState.Connected) {
        "Disconnect"
    } else {
        "Connect"
    }
    val subscribeLabel = if (mqttState.isSubscribed) "Unsubscribe" else "Subscribe"

    MagicCard(
        title = "MQTT Input",
        onInfoClick = onInfoClick,
        onReloadClick = onReloadClick,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = host,
            onValueChange = onHostChange,
            label = { Text("Broker Host") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = portText,
            onValueChange = onPortChange,
            label = { Text("Broker Port") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = insecureTls,
                onCheckedChange = onInsecureTlsChange
            )
            Text("Insecure TLS (debug, only applies to 8883)")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = clientId,
            onValueChange = onClientIdChange,
            label = { Text("Client ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = topic,
            onValueChange = onTopicChange,
            label = { Text("Topic") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = payload,
            onValueChange = onPayloadChange,
            label = { Text("Payload") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Publish")
        }
    }
}

@Composable
private fun MqttOutputCard(
    mqttState: MqttRuntimeState,
    localError: String?,
    onClearMessagesClick: () -> Unit,
    onInfoClick: () -> Unit,
    onReloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusText = when (mqttState.connectionState) {
        MqttConnectionState.Disconnected -> "Disconnected"
        MqttConnectionState.Connecting -> "Connecting"
        MqttConnectionState.Connected -> "Connected"
    }

    MagicCard(
        title = "MQTT Output",
        onInfoClick = onInfoClick,
        onReloadClick = onReloadClick,
        modifier = modifier.heightIn(min = 280.dp)
    ) {
        Text("Status: $statusText")
        Text("Subscribed Topic: ${mqttState.subscribedTopic.orEmpty()}")
        Text("Last Error: ${localError ?: mqttState.lastError ?: "-"}")
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onClearMessagesClick,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Clear")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp)
                .verticalScroll(rememberScrollState())
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
private fun MagicServiceSection(onInfoClick: () -> Unit) {
    val context = LocalContext.current
    val appVersion = BuildConfig.VERSION_NAME
    val packageName = context.packageName

    if (isWideScreen(1000)) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MagicServiceInformationCard(
                appVersion = appVersion,
                packageName = packageName,
                onInfoClick = onInfoClick,
                modifier = Modifier.weight(1f)
            )
            InvocationUrlsCard(
                onInfoClick = onInfoClick,
                modifier = Modifier.weight(1f)
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MagicServiceInformationCard(
                appVersion = appVersion,
                packageName = packageName,
                onInfoClick = onInfoClick
            )
            InvocationUrlsCard(onInfoClick = onInfoClick)
        }
    }
}

@Composable
private fun NetworkStateCard(
    state: NetworkStateUiModel,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MagicCard(
        title = "Network State",
        onInfoClick = onInfoClick,
        modifier = modifier.heightIn(min = 280.dp)
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
private fun InternetConnectivityCard(
    state: InternetCheckState,
    onInfoClick: () -> Unit,
    onReloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timestamp = state.timestamp?.let { " ($it)" }.orEmpty()
    val display = when (state.status) {
        InternetStatus.Success -> "Success$timestamp"
        InternetStatus.Error -> "Error$timestamp"
        InternetStatus.Idle -> "Not checked"
    }

    MagicCard(
        title = "Internet Connectivity",
        onInfoClick = onInfoClick,
        onReloadClick = onReloadClick,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = display,
            onValueChange = {},
            readOnly = true,
            label = { Text("HTTP request status") },
            modifier = Modifier.fillMaxWidth()
        )
        if (!state.details.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Details: ${state.details}")
        }
    }
}

@Composable
private fun OemNetworkPreferencesCard(onInfoClick: () -> Unit, modifier: Modifier = Modifier) {
    MagicCard(
        title = "OEM Network Preferences",
        onInfoClick = onInfoClick,
        modifier = modifier.heightIn(min = 260.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )
    }
}

@Composable
private fun GetTokenCard(onInfoClick: () -> Unit, modifier: Modifier = Modifier) {
    var accountName by rememberSaveable { mutableStateOf("") }
    var operationTypeId by rememberSaveable { mutableStateOf("") }

    MagicCard(
        title = "Get Token",
        onInfoClick = onInfoClick,
        onReloadClick = {},
        modifier = modifier
    ) {
        OutlinedTextField(
            value = accountName,
            onValueChange = { accountName = it },
            label = { Text("Account Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = operationTypeId,
            onValueChange = { operationTypeId = it },
            label = { Text("OperationTypeId") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {}, modifier = Modifier.align(Alignment.End)) {
            Text("Retrieve")
        }
    }
}

@Composable
private fun PlaceholderInputCard(
    title: String,
    fieldLabels: List<String>,
    onInfoClick: () -> Unit,
    onReloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var values by rememberSaveable(title) { mutableStateOf(fieldLabels.associateWith { "" }) }

    MagicCard(
        title = title,
        onInfoClick = onInfoClick,
        onReloadClick = onReloadClick,
        modifier = modifier
    ) {
        fieldLabels.forEachIndexed { index, label ->
            OutlinedTextField(
                value = values[label].orEmpty(),
                onValueChange = { text -> values = values.toMutableMap().apply { put(label, text) } },
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth()
            )
            if (index != fieldLabels.lastIndex) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun MagicServiceInformationCard(
    appVersion: String,
    packageName: String,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var status by rememberSaveable { mutableStateOf("connected") }

    MagicCard(
        title = "Information",
        onInfoClick = onInfoClick,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = status,
                onValueChange = { status = it },
                label = { Text("Status") },
                modifier = Modifier.weight(1f)
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
private fun InvocationUrlsCard(onInfoClick: () -> Unit, modifier: Modifier = Modifier) {
    var registerPairing by rememberSaveable { mutableStateOf("") }
    var mqttBackend by rememberSaveable { mutableStateOf("") }
    var tokenServer by rememberSaveable { mutableStateOf("") }

    MagicCard(
        title = "Invocation URLs",
        onInfoClick = onInfoClick,
        onReloadClick = {},
        modifier = modifier
    ) {
        OutlinedTextField(
            value = registerPairing,
            onValueChange = { registerPairing = it },
            label = { Text("Register & Pairing") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = mqttBackend,
            onValueChange = { mqttBackend = it },
            label = { Text("MQTT Backend Broker") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = tokenServer,
            onValueChange = { tokenServer = it },
            label = { Text("Token Management Authentication Server") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
