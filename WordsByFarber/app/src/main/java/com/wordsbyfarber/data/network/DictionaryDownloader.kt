package com.wordsbyfarber.data.network

// Service for downloading dictionary files from remote URLs with progress tracking
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class DictionaryDownloader(
    private val okHttpClient: OkHttpClient
) {
    companion object {
        private val TAG = DictionaryDownloader::class.java.simpleName
    }

    fun downloadDictionary(url: String): Flow<DownloadResult> = flow {
        try {
            Log.d(TAG, "Starting download from: $url")
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
            
            Log.d(TAG, "HTTP request successful, starting download...")

            // response.body is never null for successful responses
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
                    (totalBytesRead * 100 / contentLength).toInt()
                } else {
                    0
                }
                
                emit(DownloadResult.Loading(progress))
            }
            
            val fullContent = chunks.joinToString("")
            Log.d(TAG, "Download completed successfully. Total size: $totalBytesRead bytes")
            emit(DownloadResult.Success(fullContent))
            
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
    data class Success(val content: String) : DownloadResult()
    data class Error(val message: String) : DownloadResult()
}