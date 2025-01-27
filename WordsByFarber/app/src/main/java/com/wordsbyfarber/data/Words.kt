package com.wordsbyfarber.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Words(
    @PrimaryKey @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "explanation") val explanation: String
)
