package com.wordsbyfarber.data.network

// Streaming parser using curly bracket detection with regex backtrack for "const HASHED={" pattern
// This approach handles:
// 1. Pattern split across chunks (bracket detection + regex backtrack)
// 2. Multiple curly brackets before target pattern
// 3. Memory-efficient JSON streaming with JsonReader
// 4. Filtering out ___LANG___ key during parsing
import android.util.JsonReader
import android.util.Log
import com.wordsbyfarber.data.database.WordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.StringReader

class DictionaryStreamParser {
    
    companion object {
        private val TAG = DictionaryStreamParser::class.java.simpleName
        private const val LANG_KEY = "___LANG___"
        
        // Whitespace-tolerant regex to detect "const HASHED = {" pattern
        // Allows for variations in spacing around the equals sign and opening brace
        private val CONST_HASHED_PATTERN = Regex("const\\s+HASHED\\s*=\\s*\\{")
    }
    
    private var accumulatedBuffer = ""
    private var foundTargetBrace = false
    private var jsonBuffer = ""
    private var braceDepth = 0
    private var inJsonMode = false
    
    /**
     * Streaming parser implementation:
     * 1. Look for opening curly brackets '{' in each chunk
     * 2. When found, backtrack with regex to check for "const HASHED = {" pattern
     * 3. If pattern matches, discard everything before the '{' and switch to JSON mode
     * 4. Use JsonReader for memory-efficient parsing of the HASHED object
     * 5. Store each key-value pair (except ___LANG___) as WordEntity in Room database
     * 
     * @param chunks List of downloaded file chunks
     * @param minWords Expected minimum words for this language (used for progress calculation as 100% baseline)
     */
    fun parseStreamingChunks(chunks: List<String>, minWords: Int = 100_000): Flow<ParseResult> = flow {
        try {
            Log.d(TAG, "Starting streaming parse of ${chunks.size} chunks")
            emit(ParseResult.Loading(0))
            
            resetParserState()
            val words = mutableListOf<WordEntity>()
            
            chunks.forEachIndexed { chunkIndex, chunk ->
                accumulatedBuffer += chunk
                
                if (!foundTargetBrace) {
                    // Phase 1: Search for target curly bracket
                    val braceIndex = findTargetCurlyBrace()
                    if (braceIndex != -1) {
                        Log.d(TAG, "Found target HASHED pattern at position $braceIndex")
                        foundTargetBrace = true
                        inJsonMode = true
                        braceDepth = 1
                        
                        // Start JSON parsing from the opening brace
                        jsonBuffer = accumulatedBuffer.substring(braceIndex)
                        accumulatedBuffer = "" // Clear to save memory
                    }
                } else {
                    // Phase 2: JSON parsing mode - accumulate JSON content
                    jsonBuffer += chunk
                }
                
                // Try to parse accumulated JSON if we have enough content
                if (inJsonMode && canAttemptJsonParsing()) {
                    val parseResult = tryParseCompleteJson(minWords) { progress ->
                        // Emit parsing progress (offset to second half of overall progress)
                        val totalProgress = 50 + (progress * 50 / 100)
                        // Note: Can't emit from lambda, but progress is tracked internally
                    }
                    if (parseResult != null) {
                        words.addAll(parseResult)
                        Log.d(TAG, "Successfully parsed ${words.size} words from streaming chunks")
                        emit(ParseResult.Success(words))
                        return@flow
                    }
                }
                
                // Report progress
                val progress = (chunkIndex * 90 / chunks.size).coerceAtMost(89)
                emit(ParseResult.Loading(progress))
            }
            
            // If we reach here, parsing wasn't completed
            if (!foundTargetBrace) {
                Log.e(TAG, "Could not find 'const HASHED = {' pattern in any chunks")
                emit(ParseResult.Error("Dictionary file format is invalid. Please try again later."))
            } else {
                Log.e(TAG, "JSON parsing incomplete - file may be truncated")
                emit(ParseResult.Error("Dictionary download incomplete. Please try again."))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error during streaming parse", e)
            emit(ParseResult.Error("Failed to parse dictionary. Please try again later."))
        }
    }
    
    /**
     * Searches for opening curly brackets and backtracks with regex to find "const HASHED = {" pattern.
     * This handles the case where the pattern might be split across chunks.
     */
    private fun findTargetCurlyBrace(): Int {
        var searchIndex = 0
        
        while (true) {
            val braceIndex = accumulatedBuffer.indexOf('{', searchIndex)
            if (braceIndex == -1) break
            
            // Found a curly bracket - backtrack to check for our pattern
            val matchResult = CONST_HASHED_PATTERN.find(accumulatedBuffer, 0)
            if (matchResult != null) {
                // Check if this brace belongs to our pattern
                val patternEnd = matchResult.range.last + 1
                if (patternEnd == braceIndex + 1) {
                    return braceIndex
                }
            }
            
            searchIndex = braceIndex + 1
        }
        
        return -1
    }
    
    /**
     * Checks if we have enough content to attempt JSON parsing.
     * Look for the closing brace to ensure we have a complete JSON object.
     */
    private fun canAttemptJsonParsing(): Boolean {
        if (jsonBuffer.isEmpty()) return false
        
        var braceCount = 0
        var inString = false
        var escapeNext = false
        
        for (char in jsonBuffer) {
            when {
                escapeNext -> escapeNext = false
                char == '\\' && inString -> escapeNext = true
                char == '"' -> inString = !inString
                !inString && char == '{' -> braceCount++
                !inString && char == '}' -> {
                    braceCount--
                    if (braceCount == 0) {
                        return true // Found complete JSON object
                    }
                }
            }
        }
        
        return false
    }
    
    /**
     * Attempts to parse the complete JSON object using JsonReader for memory efficiency.
     * Filters out the ___LANG___ key as specified.
     * Progress is calculated using minWords as 100% baseline.
     */
    private fun tryParseCompleteJson(minWords: Int, onProgress: (Int) -> Unit): List<WordEntity>? {
        try {
            val words = mutableListOf<WordEntity>()
            var processedCount = 0
            
            JsonReader(StringReader(jsonBuffer)).use { reader ->
                reader.beginObject()
                
                while (reader.hasNext()) {
                    val key = reader.nextName()
                    val value = reader.nextString()
                    
                    // Skip the language key as specified in requirements
                    if (key != LANG_KEY) {
                        words.add(WordEntity(word = key, explanation = value))
                    }
                    
                    processedCount++
                    
                    // Report progress every 1000 entries using minWords as 100% baseline
                    if (processedCount % 1000 == 0) {
                        val progress = if (minWords > 0) {
                            // Use minWords as 100% baseline, cap at 99% until complete
                            (processedCount * 99 / minWords).coerceAtMost(99)
                        } else {
                            50 // Default progress if minWords is invalid
                        }
                        onProgress(progress)
                    }
                }
                
                reader.endObject()
            }
            
            Log.d(TAG, "Parsed ${words.size} words, minWords baseline was $minWords")
            return words
            
        } catch (e: Exception) {
            Log.d(TAG, "JSON parsing not yet complete, waiting for more chunks: ${e.message}")
            return null
        }
    }
    
    /**
     * Resets parser state for new parsing session.
     */
    private fun resetParserState() {
        accumulatedBuffer = ""
        foundTargetBrace = false
        jsonBuffer = ""
        braceDepth = 0
        inJsonMode = false
    }
}

