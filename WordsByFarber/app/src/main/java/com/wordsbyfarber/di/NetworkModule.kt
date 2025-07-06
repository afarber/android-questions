package com.wordsbyfarber.di

import com.wordsbyfarber.data.network.DictionaryDownloader
import com.wordsbyfarber.data.network.DictionaryParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DownloadTimeout

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ReadTimeout

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @DownloadTimeout
    fun provideDownloadTimeout(): Long = 300L // 5 minutes

    @Provides
    @ReadTimeout
    fun provideReadTimeout(): Long = 60L // 1 minute

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @DownloadTimeout downloadTimeout: Long,
        @ReadTimeout readTimeout: Long
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(downloadTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .writeTimeout(readTimeout, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideDictionaryDownloader(
        okHttpClient: OkHttpClient
    ): DictionaryDownloader {
        return DictionaryDownloader(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideDictionaryParser(): DictionaryParser {
        return DictionaryParser()
    }
}