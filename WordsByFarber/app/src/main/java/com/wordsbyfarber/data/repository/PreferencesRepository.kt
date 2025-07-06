package com.wordsbyfarber.data.repository

import android.content.SharedPreferences
import com.wordsbyfarber.utils.Constants

class PreferencesRepository(
    private val sharedPreferences: SharedPreferences
) {

    fun getLanguage(): String? {
        return sharedPreferences.getString(Constants.Preferences.KEY_LANGUAGE, null)
    }

    fun setLanguage(languageCode: String) {
        sharedPreferences.edit()
            .putString(Constants.Preferences.KEY_LANGUAGE, languageCode)
            .apply()
    }

    fun clearLanguage() {
        sharedPreferences.edit()
            .remove(Constants.Preferences.KEY_LANGUAGE)
            .apply()
    }

    fun getDownloadState(languageCode: String): String? {
        return sharedPreferences.getString("${Constants.Preferences.KEY_DOWNLOAD_STATE}_$languageCode", null)
    }

    fun setDownloadState(languageCode: String, state: String) {
        sharedPreferences.edit()
            .putString("${Constants.Preferences.KEY_DOWNLOAD_STATE}_$languageCode", state)
            .apply()
    }

    fun clearDownloadState(languageCode: String) {
        sharedPreferences.edit()
            .remove("${Constants.Preferences.KEY_DOWNLOAD_STATE}_$languageCode")
            .apply()
    }

    fun getBooleanPreference(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun setBooleanPreference(key: String, value: Boolean) {
        sharedPreferences.edit()
            .putBoolean(key, value)
            .apply()
    }

    fun getStringPreference(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    fun setStringPreference(key: String, value: String) {
        sharedPreferences.edit()
            .putString(key, value)
            .apply()
    }

    fun getIntPreference(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun setIntPreference(key: String, value: Int) {
        sharedPreferences.edit()
            .putInt(key, value)
            .apply()
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}