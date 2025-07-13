package com.wordsbyfarber.data.network

// Parser for extracting word data from JavaScript dictionary files
import android.util.Log
import com.wordsbyfarber.data.database.WordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DictionaryParser {
    
    companion object {
        private val TAG = DictionaryParser::class.java.simpleName
    }
    
    fun parseJavaScriptDictionary(
        content: String,
        onProgress: (Int) -> Unit = {}
    ): Flow<ParseResult> = flow {
        try {
            Log.d(TAG, "Starting to parse dictionary content (${content.length} characters)")
            emit(ParseResult.Loading(0))
            
            // Find the start of the HASHED object
            val startMarker = "const HASHED={"
            val startIndex = content.indexOf(startMarker)
            if (startIndex == -1) {
                Log.e(TAG, "Could not find 'const HASHED={' marker in content")
                emit(ParseResult.Error("Dictionary file format is invalid. Please try again later."))
                return@flow
            }
            
            // Find the end of the HASHED object
            val endMarker = "};"
            val endIndex = content.indexOf(endMarker, startIndex)
            if (endIndex == -1) {
                Log.e(TAG, "Could not find closing '};' marker in content")
                emit(ParseResult.Error("Dictionary file format is invalid. Please try again later."))
                return@flow
            }
            
            // Extract the object content
            val objectContent = content.substring(startIndex + startMarker.length, endIndex)
            
            // Parse key-value pairs using regex
            val keyValueRegex = """"([^"]+)"\s*:\s*"([^"]*)"""".toRegex()
            val matches = keyValueRegex.findAll(objectContent)
            val words = mutableListOf<WordEntity>()
            
            val matchList = matches.toList()
            val totalMatches = matchList.size
            
            matchList.forEachIndexed { index, match ->
                val key = match.groupValues[1]
                val value = match.groupValues[2]
                
                // Skip the language key
                if (key != "___LANG___") {
                    words.add(WordEntity(word = key, explanation = value))
                }
                
                // Report progress every 100 words
                if (index % 100 == 0) {
                    val progress = (index * 100 / totalMatches).coerceAtMost(99)
                    onProgress(progress)
                    emit(ParseResult.Loading(progress))
                }
            }
            
            Log.d(TAG, "Successfully parsed ${words.size} words from dictionary")
            emit(ParseResult.Success(words))
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing dictionary content", e)
            emit(ParseResult.Error("Failed to parse dictionary. Please try again later."))
        }
    }
}

sealed class ParseResult {
    data class Loading(val progress: Int) : ParseResult()
    data class Success(val words: List<WordEntity>) : ParseResult()
    data class Error(val message: String) : ParseResult()
}