package com.wordsbyfarber.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey
    val word: String,
    val explanation: String = ""
)