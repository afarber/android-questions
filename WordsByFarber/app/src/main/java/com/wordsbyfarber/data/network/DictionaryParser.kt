package com.wordsbyfarber.data.network

import com.wordsbyfarber.data.database.WordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DictionaryParser @Inject constructor() {
    
    fun parseJavaScriptDictionary(
        content: String,
        onProgress: (Int) -> Unit = {}
    ): Flow<ParseResult> = flow {
        try {
            emit(ParseResult.Loading(0))
            
            // Find the start of the HASHED object
            val startMarker = "const HASHED={"
            val startIndex = content.indexOf(startMarker)
            if (startIndex == -1) {
                emit(ParseResult.Error("Could not find 'const HASHED={' in content"))
                return@flow
            }
            
            // Find the end of the HASHED object
            val endMarker = "};"
            val endIndex = content.indexOf(endMarker, startIndex)
            if (endIndex == -1) {
                emit(ParseResult.Error("Could not find closing '};' in content"))
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
            
            emit(ParseResult.Success(words))
            
        } catch (e: Exception) {
            emit(ParseResult.Error("Parsing error: ${e.message}"))
        }
    }
}

sealed class ParseResult {
    data class Loading(val progress: Int) : ParseResult()
    data class Success(val words: List<WordEntity>) : ParseResult()
    data class Error(val message: String) : ParseResult()
}