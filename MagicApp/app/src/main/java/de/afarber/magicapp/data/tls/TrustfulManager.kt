package de.afarber.magicapp.data.tls

import android.annotation.SuppressLint
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object TrustfulManager {
    @SuppressLint("CustomX509TrustManager")
    fun trustManager(): X509TrustManager =
        object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(
                chain: Array<X509Certificate>,
                authType: String,
            ) {}

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String,
            ) {}

            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        }

    fun socketFactory(trustManager: X509TrustManager = trustManager()): SSLSocketFactory {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), SecureRandom())
        return sslContext.socketFactory
    }

    fun hostnameVerifier(): HostnameVerifier = HostnameVerifier { _, _ -> true }
}
