package com.wordsbyfarber.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Words::class], version = 300)
abstract class WordsDatabase : RoomDatabase() {
    abstract fun wordsDao(): WordsDao
}
