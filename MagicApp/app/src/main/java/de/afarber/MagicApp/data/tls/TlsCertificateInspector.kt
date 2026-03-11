package de.afarber.MagicApp.data.tls

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket

data class TlsCertificateInfo(
    val subject: String,
    val issuer: String,
    val notBefore: String,
    val notAfter: String
)

class TlsCertificateInspector {
    suspend fun fetchCertificateInfo(host: String, port: Int, trustAnyTls: Boolean): TlsCertificateInfo {
        return withContext(Dispatchers.IO) {
            require(host.isNotBlank()) { "Host is empty" }
            require(port in 1..65535) { "Port must be between 1 and 65535" }

            val socketFactory = if (trustAnyTls) {
                TrustfulManager.socketFactory()
            } else {
                SSLContext.getDefault().socketFactory
            }

            (socketFactory.createSocket(host.trim(), port) as SSLSocket).use { socket ->
                socket.soTimeout = 8_000
                socket.startHandshake()
                val certificate = socket.session.peerCertificates
                    .filterIsInstance<X509Certificate>()
                    .firstOrNull()
                    ?: error("No X509 certificate returned by server")

                TlsCertificateInfo(
                    subject = certificate.subjectX500Principal.name,
                    issuer = certificate.issuerX500Principal.name,
                    notBefore = certificate.notBefore.toString(),
                    notAfter = certificate.notAfter.toString()
                )
            }
        }
    }
}
