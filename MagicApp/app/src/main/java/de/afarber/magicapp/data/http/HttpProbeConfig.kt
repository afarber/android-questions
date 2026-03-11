package de.afarber.magicapp.data.http

data class HttpProbeConfig(
    val url: String,
    val trustAnyTls: Boolean,
    val timeoutMillis: Long = 10_000L,
)
