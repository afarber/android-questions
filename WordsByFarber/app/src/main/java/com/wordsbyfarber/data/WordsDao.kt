package com.wordsbyfarber.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordsDao {
    @Query("DELETE FROM words")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<Words>)

    @Query("SELECT COUNT(*) FROM words")
    suspend fun countAll(): Int

    @Query("SELECT * FROM words WHERE LENGTH(word) = :length")
    suspend fun getWordsByLength(length: Int): List<Words>

    @Query("SELECT * FROM words WHERE word LIKE '%' || :letter || '%'")
    suspend fun getWordsByLetter(letter: String): List<Words>
}
