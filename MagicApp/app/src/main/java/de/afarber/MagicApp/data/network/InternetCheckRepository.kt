package de.afarber.MagicApp.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InternetCheckRepository {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    suspend fun runCheck(): InternetCheckState = withContext(Dispatchers.IO) {
        val timestamp = LocalDateTime.now().format(formatter)
        var connection: HttpURLConnection? = null

        try {
            connection = (URL(TEST_URL).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6000
                readTimeout = 6000
                useCaches = false
            }

            val statusCode = connection.responseCode
            if (statusCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
                InternetCheckState(
                    status = InternetStatus.Success,
                    timestamp = timestamp
                )
            } else {
                InternetCheckState(
                    status = InternetStatus.Error,
                    timestamp = timestamp
                )
            }
        } catch (_: Exception) {
            InternetCheckState(
                status = InternetStatus.Error,
                timestamp = timestamp
            )
        } finally {
            connection?.disconnect()
        }
    }

    private companion object {
        const val TEST_URL = "http://network-test.debian.org/nm"
    }
}
