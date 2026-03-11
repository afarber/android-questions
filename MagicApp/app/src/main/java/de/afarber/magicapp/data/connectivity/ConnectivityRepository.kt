package de.afarber.magicapp.data.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class ConnectivityRepository(
    context: Context,
) {
    private val connectivityManager =
        context.getSystemService(ConnectivityManager::class.java)

    fun observeNetworkState(): Flow<NetworkStateUiModel> =
        callbackFlow {
            val manager = connectivityManager
            if (manager == null) {
                trySend(NetworkStateUiModel.unavailable())
                close()
                return@callbackFlow
            }

            fun emitState() {
                trySend(buildState(manager))
            }

            emitState()

            val callback =
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) = emitState()

                    override fun onLost(network: Network) = emitState()

                    override fun onCapabilitiesChanged(
                        network: Network,
                        networkCapabilities: NetworkCapabilities,
                    ) {
                        emitState()
                    }

                    override fun onLinkPropertiesChanged(
                        network: Network,
                        linkProperties: LinkProperties,
                    ) {
                        emitState()
                    }
                }

            manager.registerDefaultNetworkCallback(callback)
            awaitClose {
                runCatching { manager.unregisterNetworkCallback(callback) }
            }
        }.distinctUntilChanged()

    private fun buildState(manager: ConnectivityManager): NetworkStateUiModel {
        val network = manager.activeNetwork ?: return NetworkStateUiModel.unavailable()
        val capabilities = manager.getNetworkCapabilities(network)
        val linkProperties = manager.getLinkProperties(network)

        val networkId =
            if (Build.VERSION.SDK_INT >= 29) {
                network.networkHandle.toString()
            } else {
                network.toString()
            }

        val transports = capabilities?.transportSummary() ?: "UNKNOWN"
        val allCapabilities = capabilities?.capabilitySummary() ?: "UNKNOWN"
        val dnsAddresses =
            linkProperties
                ?.dnsServers
                ?.takeIf { it.isNotEmpty() }
                ?.joinToString(",") { it.hostAddress ?: it.toString() }
                ?: ""

        return NetworkStateUiModel(
            available = true,
            networkId = networkId,
            transports = transports,
            validated = capabilities?.hasCapabilityByName("NET_CAPABILITY_VALIDATED"),
            oemPaid = capabilities?.hasCapabilityByName("NET_CAPABILITY_OEM_PAID"),
            oemPrivate = capabilities?.hasCapabilityByName("NET_CAPABILITY_OEM_PRIVATE"),
            interfaceName = linkProperties?.interfaceName.orEmpty(),
            dnsAddresses = dnsAddresses,
            capabilities = allCapabilities,
        )
    }

    private fun NetworkCapabilities.transportSummary(): String {
        val transports =
            buildList {
                if (hasTransportByName("TRANSPORT_CELLULAR")) add("CELLULAR")
                if (hasTransportByName("TRANSPORT_WIFI")) add("WIFI")
                if (hasTransportByName("TRANSPORT_ETHERNET")) add("ETHERNET")
                if (hasTransportByName("TRANSPORT_BLUETOOTH")) add("BLUETOOTH")
                if (hasTransportByName("TRANSPORT_VPN")) add("VPN")
                if (hasTransportByName("TRANSPORT_USB")) add("USB")
                if (hasTransportByName("TRANSPORT_WIFI_AWARE")) add("WIFI_AWARE")
                if (hasTransportByName("TRANSPORT_LOWPAN")) add("LOWPAN")
            }
        return transports.joinToString(", ").ifBlank { "UNKNOWN" }
    }

    private fun NetworkCapabilities.capabilitySummary(): String {
        val capabilities =
            buildList {
                if (hasCapabilityByName("NET_CAPABILITY_INTERNET") == true) add("INTERNET")
                if (hasCapabilityByName("NET_CAPABILITY_TRUSTED") == true) add("TRUSTED")
                if (hasCapabilityByName("NET_CAPABILITY_NOT_VPN") == true) add("NOT_VPN")
                if (hasCapabilityByName("NET_CAPABILITY_VALIDATED") == true) add("VALIDATED")
                if (hasCapabilityByName("NET_CAPABILITY_FOREGROUND") == true) add("FOREGROUND")
                if (hasCapabilityByName("NET_CAPABILITY_NOT_CONGESTED") == true) add("NOT_CONGESTED")
                if (hasCapabilityByName("NET_CAPABILITY_NOT_SUSPENDED") == true) add("NOT_SUSPENDED")
                if (hasCapabilityByName("NET_CAPABILITY_OEM_PAID") == true) add("OEM_PAID")
                if (hasCapabilityByName("NET_CAPABILITY_OEM_PRIVATE") == true) add("OEM_PRIVATE")
                if (hasCapabilityByName("NET_CAPABILITY_NOT_VCN_MANAGED") == true) add("NOT_VCN_MANAGED")
            }
        return capabilities.joinToString(", ").ifBlank { "UNKNOWN" }
    }

    private fun NetworkCapabilities.hasCapabilityByName(name: String): Boolean? {
        val value = getNetworkCapabilityInt(name) ?: return null
        return hasCapability(value)
    }

    private fun NetworkCapabilities.hasTransportByName(name: String): Boolean {
        val value = getNetworkTransportInt(name) ?: return false
        return hasTransport(value)
    }

    private fun getNetworkCapabilityInt(name: String): Int? =
        runCatching {
            NetworkCapabilities::class.java.getField(name).getInt(null)
        }.getOrNull()

    private fun getNetworkTransportInt(name: String): Int? =
        runCatching {
            NetworkCapabilities::class.java.getField(name).getInt(null)
        }.getOrNull()
}
