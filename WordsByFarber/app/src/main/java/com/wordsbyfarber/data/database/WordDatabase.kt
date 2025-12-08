package com.wordsbyfarber.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wordsbyfarber.data.model.SupportedLanguages

@Database(entities = [WordEntity::class], version = 1)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {
        private var currentInstance: WordDatabase? = null
        private var currentLanguageCode: String? = null

        fun getInstance(context: Context, languageCode: String): WordDatabase {
            require(languageCode in SupportedLanguages.languages) {
                "Invalid language code: $languageCode. Must be one of: ${SupportedLanguages.languages.keys}"
            }

            // Close previous database if switching to a different language
            if (currentLanguageCode != null && currentLanguageCode != languageCode) {
                closeCurrentDatabase()
            }

            return currentInstance ?: Room.databaseBuilder(
                context.applicationContext,
                WordDatabase::class.java,
                "$languageCode.db"
            ).build().also {
                currentInstance = it
                currentLanguageCode = languageCode
            }
        }

        fun closeCurrentDatabase() {
            currentInstance?.close()
            currentInstance = null
            currentLanguageCode = null
        }
    }
}
