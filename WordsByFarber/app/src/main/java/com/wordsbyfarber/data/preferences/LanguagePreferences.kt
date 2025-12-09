package com.wordsbyfarber.data.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.wordsbyfarber.data.model.Language
import com.wordsbyfarber.data.model.SupportedLanguages
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private const val TAG = "WordsByFarber"

// Extension property to create a single DataStore instance per app
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object LanguagePreferences {
    private val LANGUAGE_KEY = stringPreferencesKey("language")

    /**
     * Gets the initial language for app startup.
     * Priority:
     * 1. DataStore preference (if matches SupportedLanguages)
     * 2. System locale language code (if matches SupportedLanguages)
     * 3. Default to "en"
     *
     * Uses runBlocking because this is called during ViewModel initialization.
     *
     * Locale code research:
     * - Locale.language returns ISO 639-1 language codes: "en", "de", "fr", "nl", "pl", "ru", etc.
     * - "gb", "ie" are ISO 3166-1 country codes, returned by Locale.country, not Locale.language
     * - "uk" as a language code means Ukrainian (ISO 639-1), not United Kingdom
     * - A device set to "English (United Kingdom)" returns language="en", country="GB"
     * - No mapping needed - system locale language codes match SupportedLanguages keys directly
     */
    fun getInitialLanguage(dataStore: DataStore<Preferences>, context: Context): Language {
        val savedCode = runBlocking {
            dataStore.data.first()[LANGUAGE_KEY]
        }
        Log.d(TAG, "DataStore language: $savedCode")

        // Priority 1: Saved preference
        savedCode?.let { code ->
            SupportedLanguages.languages[code]?.let {
                Log.d(TAG, "Initial language selected from DataStore: ${it.code}")
                return it
            }
        }

        // Priority 2: System locale
        val systemLocale = context.resources.configuration.locales.get(0)
        val systemLanguageCode = systemLocale.language
        Log.d(TAG, "System locale: $systemLanguageCode (country: ${systemLocale.country})")

        SupportedLanguages.languages[systemLanguageCode]?.let {
            Log.d(TAG, "Initial language selected from system locale: ${it.code}")
            return it
        }

        // Priority 3: Default to English
        val defaultLanguage = SupportedLanguages.languages["en"]!!
        Log.d(TAG, "Initial language selected (default): ${defaultLanguage.code}")
        return defaultLanguage
    }

    /**
     * Saves the selected language code to DataStore.
     */
    suspend fun saveLanguage(dataStore: DataStore<Preferences>, languageCode: String) {
        Log.d(TAG, "Saving language to DataStore: $languageCode")
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
    }
}
