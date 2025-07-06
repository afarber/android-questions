package com.wordsbyfarber.di

import android.content.Context
import androidx.room.Room
import com.wordsbyfarber.data.database.PlayerDao
import com.wordsbyfarber.data.database.WordDao
import com.wordsbyfarber.data.database.WordsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DatabaseName

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @DatabaseName
    fun provideDatabaseName(): String = "words_by_farber_db"

    @Provides
    @Singleton
    fun provideWordsDatabase(
        @ApplicationContext context: Context,
        @DatabaseName databaseName: String
    ): WordsDatabase {
        return WordsDatabase.getDatabase(context, "default")
    }

    @Provides
    fun provideWordDao(database: WordsDatabase): WordDao {
        return database.wordDao()
    }

    @Provides
    fun providePlayerDao(database: WordsDatabase): PlayerDao {
        return database.playerDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object LanguageSpecificDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabaseProvider(
        @ApplicationContext context: Context
    ): DatabaseProvider {
        return DatabaseProviderImpl(context)
    }
}

interface DatabaseProvider {
    fun getDatabase(languageCode: String): WordsDatabase
}

class DatabaseProviderImpl(
    private val context: Context
) : DatabaseProvider {
    
    private val databases = mutableMapOf<String, WordsDatabase>()
    
    override fun getDatabase(languageCode: String): WordsDatabase {
        return databases.getOrPut(languageCode) {
            WordsDatabase.getDatabase(context, languageCode)
        }
    }
}