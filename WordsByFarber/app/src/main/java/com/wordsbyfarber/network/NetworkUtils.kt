package com.wordsbyfarber.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.regex.Pattern

// Helper function to download and parse the JS file
suspend fun downloadAndParseJs(language: String): Map<String, String> {
    return withContext(Dispatchers.IO) {
        val jsonData = mutableMapOf<String, String>()
        try {
            val url = "https://wordsbyfarber.com/Consts-$language.js"
            val jsFileContent = URL(url).readText()

            val pattern = Pattern.compile("const\\s+HASHED\\s*=\\s*\\{([^}]*)\\}")
            val matcher = pattern.matcher(jsFileContent)

            if (matcher.find()) {
                val jsonString = matcher.group(1).replace("\\s+".toRegex(), "")
                val keyValuePattern = Pattern.compile("\"([^\"]+)\":\"([^\"]*)\",")
                val keyValueMatcher = keyValuePattern.matcher(jsonString)

                while (keyValueMatcher.find()) {
                    val key = keyValueMatcher.group(1)
                    val value = keyValueMatcher.group(2)
                    jsonData[key] = value
                }
            }
        } catch (e: Exception) {
            e.printStackTrace() // Log the error for debugging purposes
        }
        jsonData
    }
}
