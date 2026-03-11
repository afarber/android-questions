package de.afarber.MagicApp.data.network

enum class InternetStatus {
    Idle,
    Success,
    Error
}

data class InternetCheckState(
    val status: InternetStatus,
    val timestamp: String?
) {
    companion object {
        fun idle() = InternetCheckState(
            status = InternetStatus.Idle,
            timestamp = null
        )
    }
}
