/**
 * Room database configuration and instance management.
 *
 * This app uses a separate SQLite database file for each language (de.db, en.db, etc.)
 * to keep dictionaries isolated. This file manages the database lifecycle and ensures
 * only one database is open at a time.
 */
package com.wordsbyfarber.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wordsbyfarber.data.model.SupportedLanguages

/**
 * Room database class for dictionary storage.
 *
 * @Database annotation configures Room:
 * - entities = list of @Entity classes that become tables
 * - version = schema version number for migrations
 *
 * abstract class = Room generates the implementation at compile time
 * extends RoomDatabase = required base class for Room databases
 */
@Database(entities = [WordEntity::class], version = 1)
abstract class WordDatabase : RoomDatabase() {

    /**
     * Returns the DAO for word operations.
     *
     * abstract fun = Room generates the implementation
     */
    abstract fun wordDao(): WordDao

    /**
     * Companion object holds static members, similar to Java static methods.
     * Used here for the singleton pattern to manage database instances.
     */
    companion object {
        // Nullable type (?) allows these to be null initially
        private var currentInstance: WordDatabase? = null
        private var currentLanguageCode: String? = null

        /**
         * Returns a database instance for the specified language.
         *
         * Implements a modified singleton pattern: only one database is open at a time,
         * but switching languages closes the old database and opens a new one.
         *
         * @param context Android context needed for database file access
         * @param languageCode Language code (e.g., "de", "en") used as database filename
         * @return The database instance for the requested language
         * @throws IllegalArgumentException if languageCode is not in SupportedLanguages
         */
        fun getInstance(context: Context, languageCode: String): WordDatabase {
            // require = throws IllegalArgumentException if condition is false
            // in = Kotlin operator to check if key exists in map
            require(languageCode in SupportedLanguages.languages) {
                "Invalid language code: $languageCode. Must be one of: ${SupportedLanguages.languages.keys}"
            }

            // Close previous database if switching to a different language
            if (currentLanguageCode != null && currentLanguageCode != languageCode) {
                closeCurrentDatabase()
            }

            // Elvis operator (?:) returns left side if not null, otherwise right side
            // .also {} executes the block and returns the original object
            return currentInstance ?: Room.databaseBuilder(
                context.applicationContext,
                WordDatabase::class.java,
                // String template: "$languageCode.db" becomes "de.db", "en.db", etc.
                "$languageCode.db"
            ).build().also {
                currentInstance = it
                currentLanguageCode = languageCode
            }
        }

        /**
         * Closes the current database and clears the cached instance.
         *
         * Called when switching languages to release resources.
         */
        fun closeCurrentDatabase() {
            // Safe call operator (?.) only calls close() if currentInstance is not null
            currentInstance?.close()
            currentInstance = null
            currentLanguageCode = null
        }
    }
}
