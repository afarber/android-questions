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

/**
 * Repository that handles dictionary download and database operations.
 *
 * This class follows the Repository pattern, providing a clean API for
 * data operations while hiding the implementation details of network
 * requests and database access.
 *
 * @param context Android context needed for Room database initialization
 * @param httpClient Ktor HTTP client for downloading dictionary files
 */
class DictionaryRepository(
    private val context: Context,
    private val httpClient: HttpClient
) {
    // Json parser configured to ignore unknown keys in case the server
    // adds new fields we don't handle yet
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        /**
         * Number of words to insert per database transaction.
         * Smaller batches = more frequent progress updates but slower overall.
         * Larger batches = faster overall but less responsive progress.
         * 1000 is a good balance.
         */
        private const val BATCH_SIZE = 1000
    }

    /**
     * Downloads a dictionary and stores it in the local database.
     *
     * This function performs two main operations:
     * 1. Downloads the JavaScript file containing the dictionary
     * 2. Parses and inserts words into Room database in batches
     *
     * The JS file format is: const HASHED={"word1":"explanation1",...}
     *
     * @param language The language to download
     * @param onProgress Callback invoked after each batch insert with total words inserted
     * @return Result.success if completed, Result.failure with exception otherwise
     */
    suspend fun downloadAndStoreDictionary(
        language: Language,
        onProgress: (wordsInserted: Int) -> Unit = {}
    ): Result<Unit> {
        // runCatching wraps the block in try-catch and returns Result
        return runCatching {
            // Download the JS file (Ktor handles threading internally)
            val jsContent = httpClient.get(language.hashedDictionaryUrl).bodyAsText()

            // Switch to Default dispatcher for CPU-intensive parsing
            // This keeps the main thread free for UI updates
            withContext(Dispatchers.Default) {
                val db = WordDatabase.getInstance(context, language.code)
                // Clear existing words before inserting new ones
                db.wordDao().deleteAll()
                parseAndInsertWords(jsContent, db, onProgress)
            }
        }
    }

    /**
     * Parses JSON from the JavaScript content and inserts words in batches.
     *
     * Batching serves two purposes:
     * 1. Reduces memory pressure (don't hold all WordEntity objects at once)
     * 2. Allows progress reporting between batches
     *
     * @param jsContent The raw JavaScript file content
     * @param db The Room database instance
     * @param onProgress Callback for progress updates
     */
    private suspend fun parseAndInsertWords(
        jsContent: String,
        db: WordDatabase,
        onProgress: (wordsInserted: Int) -> Unit
    ) {
        // Extract just the JSON object from the JS file
        val jsonString = extractHashedJson(jsContent)

        // Parse the entire JSON into a JsonObject (map-like structure)
        val jsonObject = json.decodeFromString<JsonObject>(jsonString)

        // Accumulate words in a batch before inserting
        val batch = mutableListOf<WordEntity>()
        var totalInserted = 0

        // Iterate over all key-value pairs in the JSON object
        for ((word, value) in jsonObject) {
            // jsonPrimitive.content extracts the string value
            batch.add(WordEntity(word = word, explanation = value.jsonPrimitive.content))

            // When batch is full, insert and report progress
            if (batch.size >= BATCH_SIZE) {
                db.wordDao().insertWords(batch)
                totalInserted += batch.size
                onProgress(totalInserted)
                // Clear the batch to reuse the list (avoids allocations)
                batch.clear()
            }
        }

        // Insert any remaining words that didn't fill a complete batch
        if (batch.isNotEmpty()) {
            db.wordDao().insertWords(batch)
            totalInserted += batch.size
            onProgress(totalInserted)
        }
    }

    /**
     * Extracts the HASHED JSON object from JavaScript file content.
     *
     * The server returns a JS file with multiple const declarations:
     * ```
     * const COUNTRY="nl";
     * const LETTERS_EN=[...];
     * const VALUES={...};
     * const HASHED={"word1":"expl1","word2":"expl2",...};
     * ```
     *
     * This function finds "const HASHED=" and extracts the JSON object
     * by counting matching braces.
     *
     * @param jsContent The raw JavaScript file content
     * @return The JSON object string (including braces)
     * @throws IllegalArgumentException if the HASHED object cannot be found
     */
    private fun extractHashedJson(jsContent: String): String {
        val hashedMarker = "const HASHED="
        val hashedStart = jsContent.indexOf(hashedMarker)

        if (hashedStart == -1) {
            throw IllegalArgumentException("Could not find 'const HASHED=' in JavaScript content")
        }

        // Find the opening brace of the JSON object
        val jsonStart = jsContent.indexOf('{', hashedStart)
        if (jsonStart == -1) {
            throw IllegalArgumentException("Could not find opening brace after 'const HASHED='")
        }

        // Find matching closing brace by counting brace depth
        // This handles nested objects correctly (though HASHED is flat)
        var braceCount = 0
        var jsonEnd = -1
        for (i in jsonStart until jsContent.length) {
            when (jsContent[i]) {
                '{' -> braceCount++
                '}' -> {
                    braceCount--
                    if (braceCount == 0) {
                        // Found the matching closing brace
                        jsonEnd = i + 1
                        break
                    }
                }
            }
        }

        if (jsonEnd == -1) {
            throw IllegalArgumentException("Could not find matching closing brace for HASHED object")
        }

        // Extract the JSON substring
        return jsContent.substring(jsonStart, jsonEnd)
    }

    /**
     * Returns a Flow of word count for words of a specific length.
     *
     * @param languageCode The language database to query (e.g., "de", "en")
     * @param length The word length to count
     * @return Flow that emits the count whenever the database changes
     */
    fun getWordCountByLength(languageCode: String, length: Int): Flow<Int> {
        return WordDatabase.getInstance(context, languageCode).wordDao().getWordCountByLength(length)
    }

    /**
     * Returns a Flow of words with a specific length.
     *
     * @param languageCode The language database to query
     * @param length The word length to filter by
     * @return Flow that emits the list of matching WordEntity objects
     */
    fun getWordsByLength(languageCode: String, length: Int): Flow<List<WordEntity>> {
        return WordDatabase.getInstance(context, languageCode).wordDao().getWordsByLength(length)
    }

    /**
     * Returns a Flow of words containing a rare letter.
     *
     * @param languageCode The language database to query
     * @param letter The letter to search for (case-sensitive)
     * @return Flow that emits the list of matching WordEntity objects
     */
    fun getWordsByRareLetter(languageCode: String, letter: String): Flow<List<WordEntity>> {
        return WordDatabase.getInstance(context, languageCode).wordDao().getWordsByRareLetter(letter)
    }
}
