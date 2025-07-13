package com.wordsbyfarber.data.repository

// Repository providing wrapper methods for SharedPreferences operations
import android.content.SharedPreferences
import com.wordsbyfarber.utils.Constants
import androidx.core.content.edit

class PreferencesRepository(
    private val sharedPreferences: SharedPreferences
) {

    fun getLanguage(): String? {
        return sharedPreferences.getString(Constants.Preferences.KEY_LANGUAGE, null)
    }
    
    fun getCurrentLanguage(): String? {
        return getLanguage()
    }

    fun setLanguage(languageCode: String) {
        sharedPreferences.edit {
            putString(Constants.Preferences.KEY_LANGUAGE, languageCode)
        }
    }

    fun clearLanguage() {
        sharedPreferences.edit {
            remove(Constants.Preferences.KEY_LANGUAGE)
        }
    }

    fun clearAll() {
        sharedPreferences.edit { clear() }
    }
}