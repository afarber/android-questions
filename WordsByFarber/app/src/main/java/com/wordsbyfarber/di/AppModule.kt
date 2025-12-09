package com.wordsbyfarber.di

import com.wordsbyfarber.data.preferences.dataStore
import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.viewmodel.DictionaryViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

val appModule = module {
    single {
        HttpClient(Android) {
            install(HttpTimeout) {
                requestTimeoutMillis = 60.seconds.inWholeMilliseconds
                connectTimeoutMillis = 15.seconds.inWholeMilliseconds
            }
        }
    }

    single { androidContext().dataStore }

    single { DictionaryRepository(androidContext(), get()) }

    viewModel { DictionaryViewModel(get(), get(), androidContext()) }
}
