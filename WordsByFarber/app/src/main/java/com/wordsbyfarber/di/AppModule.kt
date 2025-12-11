/**
 * Koin dependency injection module definition.
 *
 * This file defines all dependencies that Koin can provide to the app.
 * Koin uses a DSL (Domain Specific Language) to declare dependencies.
 *
 * Key concepts:
 * - single {} = creates ONE instance shared across the entire app (singleton)
 * - viewModel {} = creates a new instance tied to the ViewModel lifecycle
 * - get() = asks Koin to provide a dependency that was defined elsewhere
 */
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

/**
 * Main Koin module containing all app dependencies.
 *
 * module {} is a Koin DSL function that creates a Module object.
 * This module is loaded in WordsByFarberApplication.onCreate()
 */
val appModule = module {

    // HTTP client for downloading dictionaries
    // single = only one HttpClient instance exists for the whole app
    single {
        // HttpClient(Android) creates a Ktor client using Android's networking
        HttpClient(Android) {
            // install() adds features/plugins to the client
            install(HttpTimeout) {
                // 60.seconds.inWholeMilliseconds = Kotlin duration extension
                // Converts 60 seconds to milliseconds (60000L)
                requestTimeoutMillis = 60.seconds.inWholeMilliseconds
                connectTimeoutMillis = 15.seconds.inWholeMilliseconds
            }
        }
    }

    // DataStore for persisting user preferences (selected language)
    // androidContext() = gets the Android Context provided during Koin setup
    // .dataStore = extension property defined in LanguagePreferences.kt
    single { androidContext().dataStore }

    // Repository that handles network requests and database operations
    // get() asks Koin to inject the HttpClient defined above
    single { DictionaryRepository(androidContext(), get()) }

    // ViewModel for managing UI state and business logic
    // viewModel {} = special Koin DSL for Android ViewModels
    // First get() = DictionaryRepository, second get() = DataStore
    viewModel { DictionaryViewModel(get(), get(), androidContext()) }
}
