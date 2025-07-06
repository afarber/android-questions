package com.wordsbyfarber.data.database

// Room database providing access to words and players data with language-specific instances
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
        private val INSTANCES: MutableMap<String, WordsDatabase> = mutableMapOf()

        fun getDatabase(context: Context, languageCode: String): WordsDatabase {
            return synchronized(this) {
                INSTANCES[languageCode] ?: run {
                    val instance = Room.databaseBuilder(
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
}