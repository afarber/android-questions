package com.wordsbyfarber.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    @Query("SELECT COUNT(*) FROM words WHERE LENGTH(word) = :length")
    fun getWordCountByLength(length: Int): Flow<Int>

    @Query("SELECT * FROM words WHERE LENGTH(word) = :length ORDER BY word ASC")
    fun getWordsByLength(length: Int): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE word LIKE '%' || :letter || '%' ORDER BY word ASC")
    fun getWordsByRareLetter(letter: String): Flow<List<WordEntity>>

    @Query("DELETE FROM words")
    suspend fun deleteAll()
}
