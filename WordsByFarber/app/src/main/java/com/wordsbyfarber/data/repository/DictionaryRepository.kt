package com.wordsbyfarber.data.repository

import android.content.Context
import com.wordsbyfarber.data.database.WordDatabase
import com.wordsbyfarber.data.database.WordEntity
import com.wordsbyfarber.data.model.Language
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class DictionaryRepository(
    private val context: Context,
    private val httpClient: HttpClient
) {
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private const val BATCH_SIZE = 1000
    }

    suspend fun downloadAndStoreDictionary(
        language: Language,
        onProgress: (wordsInserted: Int) -> Unit = {}
    ): Result<Unit> {
        return runCatching {
            val jsContent = httpClient.get(language.hashedDictionaryUrl).bodyAsText()
            withContext(Dispatchers.Default) {
                val db = WordDatabase.getInstance(context, language.code)
                db.wordDao().deleteAll()
                parseAndInsertWords(jsContent, db, onProgress)
            }
        }
    }

    private suspend fun parseAndInsertWords(
        jsContent: String,
        db: WordDatabase,
        onProgress: (wordsInserted: Int) -> Unit
    ) {
        val jsonString = extractHashedJson(jsContent)

        // Parse as JsonObject and insert in batches to reduce memory pressure
        val jsonObject = json.decodeFromString<JsonObject>(jsonString)
        val batch = mutableListOf<WordEntity>()
        var totalInserted = 0

        for ((word, value) in jsonObject) {
            batch.add(WordEntity(word = word, explanation = value.jsonPrimitive.content))
            if (batch.size >= BATCH_SIZE) {
                db.wordDao().insertWords(batch)
                totalInserted += batch.size
                onProgress(totalInserted)
                batch.clear()
            }
        }
        if (batch.isNotEmpty()) {
            db.wordDao().insertWords(batch)
            totalInserted += batch.size
            onProgress(totalInserted)
        }
    }

    private fun extractHashedJson(jsContent: String): String {
        val hashedMarker = "const HASHED="
        val hashedStart = jsContent.indexOf(hashedMarker)

        if (hashedStart == -1) {
            throw IllegalArgumentException("Could not find 'const HASHED=' in JavaScript content")
        }

        val jsonStart = jsContent.indexOf('{', hashedStart)
        if (jsonStart == -1) {
            throw IllegalArgumentException("Could not find opening brace after 'const HASHED='")
        }

        var braceCount = 0
        var jsonEnd = -1
        for (i in jsonStart until jsContent.length) {
            when (jsContent[i]) {
                '{' -> braceCount++
                '}' -> {
                    braceCount--
                    if (braceCount == 0) {
                        jsonEnd = i + 1
                        break
                    }
                }
            }
        }

        if (jsonEnd == -1) {
            throw IllegalArgumentException("Could not find matching closing brace for HASHED object")
        }

        return jsContent.substring(jsonStart, jsonEnd)
    }

    fun getWordCountByLength(languageCode: String, length: Int): Flow<Int> {
        return WordDatabase.getInstance(context, languageCode).wordDao().getWordCountByLength(length)
    }

    fun getWordsByLength(languageCode: String, length: Int): Flow<List<WordEntity>> {
        return WordDatabase.getInstance(context, languageCode).wordDao().getWordsByLength(length)
    }

    fun getWordsByRareLetter(languageCode: String, letter: String): Flow<List<WordEntity>> {
        return WordDatabase.getInstance(context, languageCode).wordDao().getWordsByRareLetter(letter)
    }
}
