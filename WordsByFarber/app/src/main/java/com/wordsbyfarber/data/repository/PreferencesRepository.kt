package com.wordsbyfarber.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "words_by_farber_prefs",
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_LANGUAGE = "language"
        private const val KEY_DOWNLOAD_STATE = "download_state"
    }
    
    fun setSelectedLanguage(languageCode: String) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }
    
    fun getSelectedLanguage(): String? {
        return prefs.getString(KEY_LANGUAGE, null)
    }
    
    fun clearSelectedLanguage() {
        prefs.edit().remove(KEY_LANGUAGE).apply()
    }
    
    fun setDownloadState(languageCode: String, isActive: Boolean) {
        prefs.edit().putBoolean("${KEY_DOWNLOAD_STATE}_$languageCode", isActive).apply()
    }
    
    fun isDownloadActive(languageCode: String): Boolean {
        return prefs.getBoolean("${KEY_DOWNLOAD_STATE}_$languageCode", false)
    }
    
    fun clearDownloadState(languageCode: String) {
        prefs.edit().remove("${KEY_DOWNLOAD_STATE}_$languageCode").apply()
    }
}