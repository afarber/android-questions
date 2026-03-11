package de.afarber.MagicApp.data.http

enum class HttpProbeStatus {
    Idle,
    Running,
    Success,
    Failure
}

data class HttpProbeState(
    val status: HttpProbeStatus = HttpProbeStatus.Idle,
    val timestamp: String? = null,
    val responseCode: Int? = null,
    val details: String? = null,
    val lastError: String? = null,
    val logLines: List<String> = emptyList()
)
