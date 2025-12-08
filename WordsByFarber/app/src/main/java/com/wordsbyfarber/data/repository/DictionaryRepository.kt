package com.wordsbyfarber.data.repository

import android.content.Context
import com.wordsbyfarber.data.database.WordDatabase
import com.wordsbyfarber.data.database.WordEntity
import com.wordsbyfarber.data.model.Language
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json

class DictionaryRepository(
    private val context: Context,
    private val httpClient: HttpClient
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun downloadAndStoreDictionary(language: Language): Result<Unit> {
        return runCatching {
            val jsContent = httpClient.get(language.hashedDictionaryUrl).bodyAsText()
            val words = parseHashedDictionary(jsContent)
            val db = WordDatabase.getInstance(context, language.code)
            db.wordDao().deleteAll()
            db.wordDao().insertWords(words)
        }
    }

    private fun parseHashedDictionary(jsContent: String): List<WordEntity> {
        // The JS file contains multiple const declarations:
        // const COUNTRY="nl";
        // const LETTERS_EN=[...];
        // const VALUES={...};
        // const NUMBERS={...};
        // const HASHED={"word1":"explanation1","word2":"explanation2",...}
        //
        // We need to find specifically "const HASHED=" and extract only that JSON object

        val hashedMarker = "const HASHED="
        val hashedStart = jsContent.indexOf(hashedMarker)

        if (hashedStart == -1) {
            throw IllegalArgumentException("Could not find 'const HASHED=' in JavaScript content")
        }

        // Find the opening brace after "const HASHED="
        val jsonStart = jsContent.indexOf('{', hashedStart)
        if (jsonStart == -1) {
            throw IllegalArgumentException("Could not find opening brace after 'const HASHED='")
        }

        // Find the matching closing brace by counting braces
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

        val jsonString = jsContent.substring(jsonStart, jsonEnd)

        // Parse as Map<String, String>
        val wordMap: Map<String, String> = json.decodeFromString(jsonString)

        // Convert to list of WordEntity
        return wordMap.map { (word, explanation) ->
            WordEntity(word = word, explanation = explanation)
        }
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
