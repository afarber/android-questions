package de.afarber.MagicApp.data.http

import de.afarber.MagicApp.data.tls.TrustfulManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout

object KtorHttpClientFactory {
    fun create(trustAnyTls: Boolean, timeoutMillis: Long): HttpClient {
        return HttpClient(OkHttp) {
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
}
