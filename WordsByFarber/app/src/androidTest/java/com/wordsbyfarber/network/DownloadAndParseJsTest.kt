package com.wordsbyfarber.network

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.wordsbyfarber.data.WordsDao
import com.wordsbyfarber.data.WordsDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLStreamHandler
import java.net.URLStreamHandlerFactory

@RunWith(AndroidJUnit4::class)
@SmallTest
class DownloadAndParseJsTest {

    private lateinit var database: WordsDatabase
    private lateinit var wordsDao: WordsDao

    @Before
    fun setup() {
        // Initialize the in-memory database
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WordsDatabase::class.java
        ).allowMainThreadQueries().build()
        wordsDao = database.wordsDao()
    }

    @After
    fun teardown() {
        // Close the database after the test
        database.close()
    }

    @Test
    fun testDownloadAndParseJs() = runBlocking {
        // Sample test data
        val testData = """
            blah blah blah 
            blah const HASHED ={
            "word1": "explanation1",
            "word2"  
              :
              "",
            "word3":"explanation3",
            blah blah blah
        """.trimIndent()

        // Set the URLStreamHandlerFactory to intercept HTTP requests
        URL.setURLStreamHandlerFactory(object : URLStreamHandlerFactory {
            override fun createURLStreamHandler(protocol: String): URLStreamHandler? {
                return if ("https" == protocol) {
                    object : URLStreamHandler() {
                        override fun openConnection(url: URL?): HttpURLConnection {
                            return object : HttpURLConnection(url) {
                                override fun connect() {}
                                override fun disconnect() {}
                                override fun usingProxy() = false
                                override fun getInputStream() = ByteArrayInputStream(testData.toByteArray())
                            }
                        }
                    }
                } else {
                    null
                }
            }
        })

        // Language to use in the test
        val language = "en"

        // Call the method with the language argument
        downloadAndParseJs(language, wordsDao)

        // Verify the results in the database
        val result = wordsDao.countAll()
        assertEquals(3, result)

        val words = wordsDao.getWordsByLetter("w")
        assertEquals(3, words.size)

        // Verify each word and its explanation
        assertTrue(words.any { it.word == "word1" && it.explanation == "explanation1" })
        assertTrue(words.any { it.word == "word2" && it.explanation == "" })
        assertTrue(words.any { it.word == "word3" && it.explanation == "explanation3" })
    }
}
