package de.afarber.MagicApp.data.connectivity

data class NetworkStateUiModel(
    val available: Boolean,
    val networkId: String,
    val transports: String,
    val validated: Boolean?,
    val oemPaid: Boolean?,
    val oemPrivate: Boolean?,
    val interfaceName: String,
    val dnsAddresses: String,
    val capabilities: String
) {
    companion object {
        fun unavailable() = NetworkStateUiModel(
            available = false,
            networkId = "",
            transports = "",
            validated = null,
            oemPaid = null,
            oemPrivate = null,
            interfaceName = "",
            dnsAddresses = "",
            capabilities = ""
        )
    }
}
