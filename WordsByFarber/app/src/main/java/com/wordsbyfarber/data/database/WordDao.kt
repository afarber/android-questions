/**
 * Room Data Access Object (DAO) for word operations.
 *
 * This interface defines all database operations for the words table.
 * Room generates the implementation at compile time using annotation processing.
 * Methods can be either suspend functions (for one-shot operations) or
 * return Flow (for observable queries that update when data changes).
 */
package com.wordsbyfarber.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for dictionary word operations.
 *
 * @Dao tells Room this interface defines database access methods.
 * Room will generate the actual implementation code at compile time.
 */
@Dao
interface WordDao {

    /**
     * Inserts a batch of words into the database.
     *
     * @param words List of WordEntity objects to insert
     *
     * suspend = this is a coroutine function, must be called from a coroutine
     * OnConflictStrategy.REPLACE = if a word already exists, replace it with new data
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    /**
     * Returns the count of words with a specific length.
     *
     * @param length The word length to count (e.g., 2 for two-letter words)
     * @return Flow that emits the count; updates automatically when table changes
     *
     * Flow = reactive stream that emits new values when the underlying data changes
     * No suspend = Room handles the threading internally for Flow-returning methods
     */
    @Query("SELECT COUNT(*) FROM words WHERE LENGTH(word) = :length")
    fun getWordCountByLength(length: Int): Flow<Int>

    /**
     * Returns all words with a specific length, sorted alphabetically.
     *
     * @param length The word length to filter by
     * @return Flow of word list; updates when table changes
     */
    @Query("SELECT * FROM words WHERE LENGTH(word) = :length ORDER BY word ASC")
    fun getWordsByLength(length: Int): Flow<List<WordEntity>>

    /**
     * Returns all words containing a specific letter.
     *
     * Useful for finding words with rare letters (Q, X, Z, etc.) in Scrabble.
     *
     * @param letter The letter to search for (case-sensitive)
     * @return Flow of matching words
     *
     * SQL LIKE with || = string concatenation, so '%' || :letter || '%'
     * becomes '%Q%' which matches any word containing Q
     */
    @Query("SELECT * FROM words WHERE word LIKE '%' || :letter || '%' ORDER BY word ASC")
    fun getWordsByRareLetter(letter: String): Flow<List<WordEntity>>

    /**
     * Deletes all words from the table.
     *
     * Called before downloading a new dictionary to ensure fresh data.
     */
    @Query("DELETE FROM words")
    suspend fun deleteAll()
}
