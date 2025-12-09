package com.wordsbyfarber.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * Utility object for managing app-wide locale/language settings.
 *
 * This uses AndroidX AppCompat's per-app language preferences API which:
 * - Works on all Android versions (backported to API 21+)
 * - On Android 13+ (API 33): Delegates to the platform implementation
 * - On older versions: Handles locale storage and activity recreation internally
 *
 * The locale change takes effect immediately and Compose's stringResource()
 * will automatically use the new locale.
 */
object LocaleManager {

    /**
     * Changes the app's UI language at runtime.
     *
     * This should be called from the main thread. The change is persisted
     * automatically by AppCompatDelegate.
     *
     * @param languageCode BCP47 language tag (e.g., "de", "en", "fr", "nl", "pl", "ru")
     */
    fun setLanguage(languageCode: String) {
        // Create a LocaleListCompat from the language tag
        // forLanguageTags parses BCP47 format like "en-US" or simple "en"
        val localeList = LocaleListCompat.forLanguageTags(languageCode)

        // Apply the locale to the app
        // This triggers UI refresh on Android 12+ or activity recreation on older versions
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}
