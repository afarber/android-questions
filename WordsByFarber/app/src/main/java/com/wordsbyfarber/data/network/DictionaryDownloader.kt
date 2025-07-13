package com.wordsbyfarber.data.network

// Service for downloading dictionary files from remote URLs with streaming chunk processing
// Integrates with DictionaryStreamParser for memory-efficient parsing during download
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class DictionaryDownloader(
    private val okHttpClient: OkHttpClient,
    private val streamParser: DictionaryStreamParser = DictionaryStreamParser()
) {
    companion object {
        private val TAG = DictionaryDownloader::class.java.simpleName
    }

    /**
     * Downloads dictionary file and streams chunks to parser for memory-efficient processing.
     * Uses curly bracket detection with regex backtrack approach to find "const HASHED={" pattern.
     * 
     * @param url URL to download dictionary from
     * @param minWords Expected minimum words for this language (used as 100% baseline for parsing progress)
     */
    fun downloadAndParseDictionary(url: String, minWords: Int = 100_000): Flow<DownloadResult> = flow {
        try {
            Log.d(TAG, "Starting streaming download and parse from: $url")
            emit(DownloadResult.Loading(0))
            
            val request = Request.Builder()
                .url(url)
                .build()
            
            Log.d(TAG, "Executing HTTP request...")
            val response = okHttpClient.newCall(request).execute()
            
            if (!response.isSuccessful) {
                val errorMsg = "HTTP ${response.code}: ${response.message}"
                Log.e(TAG, "HTTP request failed: $errorMsg")
                emit(DownloadResult.Error(errorMsg))
                return@flow
            }
            
            Log.d(TAG, "HTTP request successful, starting streaming download...")

            val contentLength = response.body.contentLength()
            val inputStream = response.body.byteStream()
            val chunks = mutableListOf<String>()
            
            Log.d(TAG, "Content length: $contentLength bytes")
            var totalBytesRead = 0L
            val buffer = ByteArray(8192)
            
            while (true) {
                val bytesRead = inputStream.read(buffer)
                if (bytesRead == -1) break
                
                totalBytesRead += bytesRead
                val chunk = String(buffer, 0, bytesRead, Charsets.UTF_8)
                chunks.add(chunk)
                
                val progress = if (contentLength > 0) {
                    (totalBytesRead * 50 / contentLength).toInt() // Reserve 50% for download, 50% for parsing
                } else {
                    0
                }
                
                emit(DownloadResult.Loading(progress))
            }
            
            Log.d(TAG, "Download completed, starting streaming parse of ${chunks.size} chunks")
            
            // Stream the chunks to the parser using minWords as progress baseline
            streamParser.parseStreamingChunks(chunks, minWords).collect { parseResult ->
                when (parseResult) {
                    is ParseResult.Loading -> {
                        // Offset parsing progress to second half (50-100%)
                        val totalProgress = 50 + (parseResult.progress * 50 / 100)
                        emit(DownloadResult.Loading(totalProgress))
                    }
                    is ParseResult.Success -> {
                        Log.d(TAG, "Streaming parse completed successfully with ${parseResult.words.size} words")
                        emit(DownloadResult.Success(parseResult.words))
                    }
                    is ParseResult.Error -> {
                        Log.e(TAG, "Streaming parse failed: ${parseResult.message}")
                        emit(DownloadResult.Error(parseResult.message))
                    }
                }
            }
            
        } catch (e: IOException) {
            Log.e(TAG, "Network error during download from $url", e)
            emit(DownloadResult.Error("Failed to download dictionary. Please check your internet connection and try again."))
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission error during download from $url", e)
            emit(DownloadResult.Error("Failed to download dictionary. Please try again later."))
        } catch (e: android.os.NetworkOnMainThreadException) {
            Log.e(TAG, "CRITICAL: Network operation attempted on main thread. This should not happen with flowOn(Dispatchers.IO).", e)
            emit(DownloadResult.Error("Failed to download dictionary. Please try again later."))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during download from $url: ${e.javaClass.simpleName}", e)
            emit(DownloadResult.Error("Failed to download dictionary. Please try again later."))
        }
    }.flowOn(Dispatchers.IO)
}

sealed class DownloadResult {
    data class Loading(val progress: Int) : DownloadResult()
    data class Success(val words: List<com.wordsbyfarber.data.database.WordEntity>) : DownloadResult()
    data class Error(val message: String) : DownloadResult()
}