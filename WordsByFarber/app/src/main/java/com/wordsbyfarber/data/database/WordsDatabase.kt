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
        private val INSTANCES: MutableMap<String, WordsDatabase> = mutableMapOf()

        fun getDatabase(context: Context, languageCode: String): WordsDatabase {
            return INSTANCES[languageCode] ?: synchronized(this) {
                val instance = INSTANCES[languageCode] ?: Room.databaseBuilder(
                    context.applicationContext,
                    WordsDatabase::class.java,
                    "words_database_$languageCode"
                ).build()
                INSTANCES[languageCode] = instance
                instance
            }
        }
    }
}