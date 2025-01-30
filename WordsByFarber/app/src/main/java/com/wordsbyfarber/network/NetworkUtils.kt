package com.wordsbyfarber.network

import android.util.Log
import com.wordsbyfarber.data.Words
import com.wordsbyfarber.data.WordsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.regex.Pattern

// Helper function to download and parse the JS file in chunks
suspend fun downloadAndParseJs(language: String, wordsDao: WordsDao) {
    withContext(Dispatchers.IO) {
        try {
            val url = "https://wordsbyfarber.com/Consts-${language}.js"
            val inputStream = URL(url).openStream()
            val buffer = ByteArray(128 * 1024) // 128 kbytes buffer
            // match the "const HASHED = {" and ignore spaces
            val hashedPattern = Pattern.compile(
                """
                const\s+HASHED\s*=\s*\{([^}]*)\}
                """.trimIndent(), Pattern.MULTILINE
            )
            // match the "word": "possibly empty explanation",
            val keyValuePattern = Pattern.compile(
                """
                "([^"]+)"\s*:\s*"([^"]*)",
                """.trimIndent(), Pattern.MULTILINE
            )
            var content = StringBuilder()
            var bytesRead: Int
            var hashedFound = false

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                content.append(String(buffer, 0, bytesRead))

                if (!hashedFound) {
                    val matcher = hashedPattern.matcher(content)
                    if (matcher.find()) {
                        hashedFound = true
                        Log.d("DownloadAndParseJs", "HASHED pattern found.")
                        content = StringBuilder(content.substring(matcher.end()))
                    }
                }

                if (hashedFound) {
                    val keyValueMatcher = keyValuePattern.matcher(content)

                    while (keyValueMatcher.find()) {
                        val key = keyValueMatcher.group(1)
                        val value = keyValueMatcher.group(2)
                        wordsDao.insert(Words(key, value))
                    }

                    // Preserve leftover content
                    content = StringBuilder(content.substring(keyValueMatcher.regionStart()))

                    // Log periodically
                    Log.d("DownloadAndParseJs", "Processed a chunk.")
                }
            }
            inputStream.close()
            Log.d("DownloadAndParseJs", "Parsing complete.")
        } catch (ex: Exception) {
            Log.e("DownloadAndParseJs", "Error: ${ex.message}", ex)
            ex.printStackTrace()
        }
    }
}
