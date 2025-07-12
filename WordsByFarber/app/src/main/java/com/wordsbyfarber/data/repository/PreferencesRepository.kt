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

    fun getDownloadState(languageCode: String): String? {
        return sharedPreferences.getString("${Constants.Preferences.KEY_DOWNLOAD_STATE}_$languageCode", null)
    }

    fun setDownloadState(languageCode: String, state: String) {
        sharedPreferences.edit {
            putString("${Constants.Preferences.KEY_DOWNLOAD_STATE}_$languageCode", state)
        }
    }

    fun clearDownloadState(languageCode: String) {
        sharedPreferences.edit {
            remove("${Constants.Preferences.KEY_DOWNLOAD_STATE}_$languageCode")
        }
    }

    fun getBooleanPreference(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun setBooleanPreference(key: String, value: Boolean) {
        sharedPreferences.edit {
            putBoolean(key, value)
        }
    }

    fun getStringPreference(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    fun setStringPreference(key: String, value: String) {
        sharedPreferences.edit {
            putString(key, value)
        }
    }

    fun getIntPreference(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun setIntPreference(key: String, value: Int) {
        sharedPreferences.edit {
            putInt(key, value)
        }
    }

    fun clearAll() {
        sharedPreferences.edit { clear() }
    }
}