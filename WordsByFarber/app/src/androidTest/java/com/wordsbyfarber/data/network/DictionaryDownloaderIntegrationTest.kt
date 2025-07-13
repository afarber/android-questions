package com.wordsbyfarber.data.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes

@RunWith(AndroidJUnit4::class)
class DictionaryDownloaderIntegrationTest {
    
    private lateinit var downloader: DictionaryDownloader
    private lateinit var okHttpClient: OkHttpClient
    
    @Before
    fun setUp() {
        // Configure OkHttp client with longer timeouts for large file downloads
        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
        
        downloader = DictionaryDownloader(okHttpClient)
    }
    
    @Test
    fun `downloadAndParseDictionary should successfully download and parse Consts-pl_js`() = runTest(timeout = 2.minutes) { // 2 minute timeout
        // Polish dictionary URL as specified in CLAUDE.md
        val url = "https://wordsbyfarber.com/Consts-pl.js"
        
        val results = downloader.downloadAndParseDictionary(url).toList()
        
        // Should have multiple loading states and final success
        assertTrue("Should have multiple progress updates", results.size >= 3)
        
        // Should have loading states with increasing progress
        val loadingResults = results.filterIsInstance<DownloadResult.Loading>()
        assertTrue("Should have loading progress updates", loadingResults.isNotEmpty())
        
        // Progress should generally increase (allowing for parsing offset)
        for (i in 1 until loadingResults.size) {
            assertTrue("Progress should generally increase or stay same", 
                loadingResults[i].progress >= loadingResults[i-1].progress - 1) // Allow small decreases due to rounding
        }
        
        // Last result should be success
        val lastResult = results.last()
        assertTrue("Download should succeed", lastResult is DownloadResult.Success)
        
        val successResult = lastResult as DownloadResult.Success
        val words = successResult.words
        
        // Polish dictionary should have many words (3,000,000 according to CLAUDE.md)
        // Allow for some variation but expect a substantial number
        assertTrue("Polish dictionary should have many words (expecting > 100,000)", 
            words.size > 100_000)
        
        // Verify no ___LANG___ key is present
        assertTrue("Should not contain ___LANG___ key", 
            words.none { it.word == "___LANG___" })
        
        // Verify all words have valid structure
        words.forEach { word ->
            assertTrue("Word should not be empty", word.word.isNotEmpty())
            // Explanation can be empty string, that's valid
        }
        
        // Check for some expected characteristics of Polish dictionary
        // Polish has hashed words (16-char hex) and clear words with Polish characters
        val hashedWords = words.filter { it.word.length == 16 && it.word.all { c -> c.isDigit() || c in 'a'..'f' } }
        val clearWords = words.filter { it.word.length <= 3 || it.word.contains(Regex("[ĄĆĘŁŃÓŚŹŻąćęłńóśźż]")) }
        
        assertTrue("Should have hashed words", hashedWords.isNotEmpty())
        assertTrue("Should have clear Polish words", clearWords.isNotEmpty())
        
        println("Integration test completed successfully:")
        println("- Total words downloaded: ${words.size}")
        println("- Hashed words: ${hashedWords.size}")
        println("- Clear words (short or with Polish chars): ${clearWords.size}")
        println("- Progress updates received: ${loadingResults.size}")
    }
    
    @Test
    fun `downloadAndParseDictionary should handle network errors gracefully`() = runTest {
        // Use invalid URL to test error handling
        val invalidUrl = "https://wordsbyfarber.com/non-existent-file.js"
        
        val results = downloader.downloadAndParseDictionary(invalidUrl).toList()
        
        // Should have at least one result
        assertTrue("Should have at least one result", results.isNotEmpty())
        
        // Last result should be error for non-existent file
        val lastResult = results.last()
        assertTrue("Should fail for non-existent file", lastResult is DownloadResult.Error)
        
        val errorResult = lastResult as DownloadResult.Error
        assertTrue("Error message should be meaningful", errorResult.message.isNotEmpty())
    }
    
    @Test
    fun `downloadAndParseDictionary should handle invalid dictionary format`() = runTest {
        // Test with a URL that returns valid HTTP but invalid dictionary format
        // Using a small text file that doesn't have the HASHED pattern
        val invalidFormatUrl = "https://httpbin.org/robots.txt"
        
        val results = downloader.downloadAndParseDictionary(invalidFormatUrl).toList()
        
        // Should complete with an error about invalid format
        val lastResult = results.last()
        assertTrue("Should fail for invalid format", lastResult is DownloadResult.Error)
        
        val errorResult = lastResult as DownloadResult.Error
        assertTrue("Error should mention invalid format", 
            errorResult.message.contains("invalid", ignoreCase = true))
    }
}