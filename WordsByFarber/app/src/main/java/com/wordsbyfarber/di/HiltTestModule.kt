package com.wordsbyfarber.di

import android.content.Context
import androidx.room.Room
import com.wordsbyfarber.data.database.WordsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object HiltTestDatabaseModule {

    @Provides
    @Singleton
    fun provideInMemoryDatabase(@ApplicationContext context: Context): WordsDatabase {
        return Room.inMemoryDatabaseBuilder(
            context,
            WordsDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Provides
    @Singleton
    fun provideTestDatabaseProvider(
        @ApplicationContext context: Context
    ): DatabaseProvider {
        return TestDatabaseProvider(context)
    }
}

class TestDatabaseProvider(
    private val context: Context
) : DatabaseProvider {
    
    private val databases = mutableMapOf<String, WordsDatabase>()
    
    override fun getDatabase(languageCode: String): WordsDatabase {
        return databases.getOrPut(languageCode) {
            Room.inMemoryDatabaseBuilder(
                context,
                WordsDatabase::class.java
            ).allowMainThreadQueries().build()
        }
    }
}