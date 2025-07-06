package com.wordsbyfarber.data.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class DictionaryDownloader(
    private val okHttpClient: OkHttpClient
) {
    fun downloadDictionary(url: String): Flow<DownloadResult> = flow {
        try {
            emit(DownloadResult.Loading(0))
            
            val request = Request.Builder()
                .url(url)
                .build()
            
            val response = okHttpClient.newCall(request).execute()
            
            if (!response.isSuccessful) {
                emit(DownloadResult.Error("HTTP ${response.code}: ${response.message}"))
                return@flow
            }
            
            val responseBody = response.body
            if (responseBody == null) {
                emit(DownloadResult.Error("Empty response body"))
                return@flow
            }
            
            val contentLength = responseBody.contentLength()
            val inputStream = responseBody.byteStream()
            val chunks = mutableListOf<String>()
            
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
            emit(DownloadResult.Success(fullContent))
            
        } catch (e: IOException) {
            emit(DownloadResult.Error("Network error: ${e.message}"))
        } catch (e: Exception) {
            emit(DownloadResult.Error("Unexpected error: ${e.message}"))
        }
    }
}

sealed class DownloadResult {
    data class Loading(val progress: Int) : DownloadResult()
    data class Success(val content: String) : DownloadResult()
    data class Error(val message: String) : DownloadResult()
}