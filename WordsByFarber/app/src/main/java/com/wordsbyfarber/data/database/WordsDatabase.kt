package com.wordsbyfarber.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [WordEntity::class, PlayerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WordsDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun playerDao(): PlayerDao

    companion object {
        @Volatile
        private var INSTANCE: WordsDatabase? = null

        fun getDatabase(context: Context, databaseName: String): WordsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordsDatabase::class.java,
                    databaseName
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}