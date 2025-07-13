package com.wordsbyfarber.data.network

// Test version of DictionaryStreamParser without Android Log dependencies
import android.util.JsonReader
import com.wordsbyfarber.data.database.WordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.StringReader

class TestDictionaryStreamParser {
    
    companion object {
        private val TAG = TestDictionaryStreamParser::class.java.simpleName
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
     * Streaming parser implementation for testing without Android Log dependencies.
     * Same logic as DictionaryStreamParser but uses println instead of Log.
     */
    fun parseStreamingChunks(chunks: List<String>, minWords: Int = 100_000): Flow<ParseResult> = flow {
        try {
            println("$TAG: Starting streaming parse of ${chunks.size} chunks")
            emit(ParseResult.Loading(0))
            
            resetParserState()
            val words = mutableListOf<WordEntity>()
            
            chunks.forEachIndexed { chunkIndex, chunk ->
                accumulatedBuffer += chunk
                
                if (!foundTargetBrace) {
                    // Phase 1: Search for target curly bracket
                    val braceIndex = findTargetCurlyBrace()
                    if (braceIndex != -1) {
                        println("$TAG: Found target HASHED pattern at position $braceIndex")
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
                        println("$TAG: Successfully parsed ${words.size} words from streaming chunks")
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
                println("$TAG: Could not find 'const HASHED = {' pattern in any chunks")
                emit(ParseResult.Error("Dictionary file format is invalid. Please try again later."))
            } else {
                println("$TAG: JSON parsing incomplete - file may be truncated")
                emit(ParseResult.Error("Dictionary download incomplete. Please try again."))
            }
            
        } catch (e: Exception) {
            println("$TAG: Error during streaming parse: ${e.message}")
            e.printStackTrace()
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
            
            println("$TAG: Parsed ${words.size} words, minWords baseline was $minWords")
            return words
            
        } catch (e: Exception) {
            println("$TAG: JSON parsing not yet complete, waiting for more chunks: ${e.message}")
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