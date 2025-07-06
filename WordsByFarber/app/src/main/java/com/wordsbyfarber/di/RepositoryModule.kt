package com.wordsbyfarber.di

import android.content.Context
import android.content.SharedPreferences
import com.wordsbyfarber.data.network.DictionaryDownloader
import com.wordsbyfarber.data.network.DictionaryParser
import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.PreferencesRepository
import com.wordsbyfarber.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppSharedPreferences

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @AppSharedPreferences
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(
            Constants.Preferences.PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(
        @AppSharedPreferences sharedPreferences: SharedPreferences
    ): PreferencesRepository {
        return PreferencesRepository(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideDictionaryRepository(
        @ApplicationContext context: Context,
        databaseProvider: DatabaseProvider,
        downloader: DictionaryDownloader,
        parser: DictionaryParser
    ): DictionaryRepository {
        return DictionaryRepository(
            context = context,
            databaseProvider = databaseProvider,
            downloader = downloader,
            parser = parser
        )
    }
}