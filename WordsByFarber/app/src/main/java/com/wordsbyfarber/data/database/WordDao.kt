package com.wordsbyfarber.data.database

// Room DAO for accessing and manipulating word data in the database
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity)

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getWordCount(): Int

    @Query("SELECT * FROM words WHERE word LIKE :searchQuery || '%' ORDER BY word ASC")
    fun searchWords(searchQuery: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE LENGTH(word) = :length ORDER BY word ASC")
    fun getWordsByLength(length: Int): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE word LIKE '%' || :letter || '%' ORDER BY word ASC")
    fun getWordsByRareLetter(letter: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE word = :word LIMIT 1")
    suspend fun findWord(word: String): WordEntity?

    @Query("DELETE FROM words")
    suspend fun deleteAllWords()
}