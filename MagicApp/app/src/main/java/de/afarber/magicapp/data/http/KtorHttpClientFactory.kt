package de.afarber.magicapp.data.http

import de.afarber.magicapp.data.tls.TrustfulManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout

object KtorHttpClientFactory {
    fun create(
        trustAnyTls: Boolean,
        timeoutMillis: Long,
    ): HttpClient =
        HttpClient(OkHttp) {
            expectSuccess = false
            install(HttpTimeout) {
                requestTimeoutMillis = timeoutMillis
                connectTimeoutMillis = timeoutMillis
                socketTimeoutMillis = timeoutMillis
            }
            engine {
                config {
                    retryOnConnectionFailure(true)
                    if (trustAnyTls) {
                        val trustManager = TrustfulManager.trustManager()
                        sslSocketFactory(TrustfulManager.socketFactory(trustManager), trustManager)
                        hostnameVerifier(TrustfulManager.hostnameVerifier())
                    }
                }
            }
        }
}
