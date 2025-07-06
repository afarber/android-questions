package com.wordsbyfarber.data.database

// Room entity representing a word in the dictionary with optional explanation
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey
    val word: String,
    val explanation: String = ""
)