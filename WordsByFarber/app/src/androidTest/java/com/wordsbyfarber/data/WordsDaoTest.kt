package com.wordsbyfarber.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.wordsbyfarber.data.Words
import com.wordsbyfarber.data.WordsDao
import com.wordsbyfarber.data.WordsDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class WordsDaoTest {
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
    fun testGetWordsByLength() = runBlocking {
        val testWords = listOf(
            Words(word = "a", explanation = "Has 1 letter"),
            Words(word = "ab", explanation = "Has 2 letters"),
            Words(word = "abc", explanation = "Has 3 letters"),
            Words(word = "abcd", explanation = "Has 4 letters")
        )
        wordsDao.insertAll(testWords)

        val result = wordsDao.getWordsByLength(3)
        assertEquals(1, result.size)
        assertEquals("abc", result[0].word)
    }

    @Test
    fun testGetWordsByLetter() = runBlocking {
        val testWords = listOf(
            Words(word = "apple", explanation = "Has an A in it"),
            Words(word = "banana", explanation = "Has an A in it"),
            Words(word = "cherry", explanation =  ""),
            Words(word = "date", explanation = "Has an A in it")
        )
        wordsDao.insertAll(testWords)

        val result = wordsDao.getWordsByLetter("a")
        assertEquals(3, result.size)
        assertTrue(result.any { it.word == "apple" })
        assertTrue(result.any { it.word == "banana" })
        assertTrue(result.any { it.word == "date" })
    }
}
