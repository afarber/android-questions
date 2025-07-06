package com.wordsbyfarber.di

import android.content.Context
import com.wordsbyfarber.utils.NetworkUtils
import com.wordsbyfarber.utils.StringUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppContext

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @AppContext
    fun provideAppContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideNetworkUtils(@ApplicationContext context: Context): NetworkUtils {
        return NetworkUtils(context)
    }

    @Provides
    @Singleton
    fun provideStringUtils(): StringUtils {
        return StringUtils
    }
}

/**
 * This module aggregates all the key components for easy dependency injection.
 * It serves as a central point to understand the app's dependency graph.
 */
@Module(
    includes = [
        DatabaseModule::class,
        LanguageSpecificDatabaseModule::class,
        NetworkModule::class,
        RepositoryModule::class,
        UseCaseModule::class
    ]
)
@InstallIn(SingletonComponent::class)
object AllModules