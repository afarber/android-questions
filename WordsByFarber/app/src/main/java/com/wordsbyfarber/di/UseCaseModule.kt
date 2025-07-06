package com.wordsbyfarber.di

import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.PreferencesRepository
import com.wordsbyfarber.domain.usecases.DownloadDictionaryUseCase
import com.wordsbyfarber.domain.usecases.GetLanguagesUseCase
import com.wordsbyfarber.domain.usecases.GetWordsUseCase
import com.wordsbyfarber.domain.usecases.SearchWordsUseCase
import com.wordsbyfarber.domain.usecases.SelectLanguageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetLanguagesUseCase(
        dictionaryRepository: DictionaryRepository
    ): GetLanguagesUseCase {
        return GetLanguagesUseCase(dictionaryRepository)
    }

    @Provides
    @Singleton
    fun provideSelectLanguageUseCase(
        dictionaryRepository: DictionaryRepository,
        preferencesRepository: PreferencesRepository
    ): SelectLanguageUseCase {
        return SelectLanguageUseCase(dictionaryRepository, preferencesRepository)
    }

    @Provides
    @Singleton
    fun provideDownloadDictionaryUseCase(
        dictionaryRepository: DictionaryRepository
    ): DownloadDictionaryUseCase {
        return DownloadDictionaryUseCase(dictionaryRepository)
    }

    @Provides
    @Singleton
    fun provideSearchWordsUseCase(
        dictionaryRepository: DictionaryRepository
    ): SearchWordsUseCase {
        return SearchWordsUseCase(dictionaryRepository)
    }

    @Provides
    @Singleton
    fun provideGetWordsUseCase(
        dictionaryRepository: DictionaryRepository
    ): GetWordsUseCase {
        return GetWordsUseCase(dictionaryRepository)
    }
}