package com.wordsbyfarber.viewmodel

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordsbyfarber.data.model.Language
import com.wordsbyfarber.data.preferences.LanguagePreferences
import com.wordsbyfarber.data.repository.DictionaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "WordsByFarber"

/**
 * ViewModel that manages dictionary download and data access.
 *
 * This ViewModel survives configuration changes (like screen rotation) and
 * provides a single source of truth for the download state. It uses Koin
 * for dependency injection of the repository.
 *
 * @param repository The repository that handles network and database operations
 * @param dataStore DataStore for persisting language preference
 * @param context Application context for reading system locale
 */
class DictionaryViewModel(
    private val repository: DictionaryRepository,
    private val dataStore: DataStore<Preferences>,
    context: Context
) : ViewModel() {

    /**
     * The currently selected language for dictionary operations.
     * Non-nullable - initialized from DataStore, system locale, or default "en".
     * Private setter ensures only this ViewModel can modify it.
     */
    var selectedLanguage: Language
        private set

    // Backing property pattern: _downloadState is mutable internally,
    // downloadState is read-only externally
    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)

    /**
     * Observable state of the download process.
     * UI observes this to update progress indicators and navigate on completion.
     */
    val downloadState: StateFlow<DownloadState> = _downloadState

    init {
        selectedLanguage = LanguagePreferences.getInitialLanguage(dataStore, context)
        Log.d(TAG, "ViewModel init: selectedLanguage=${selectedLanguage.code}")
    }

    /**
     * Sets the language for subsequent dictionary operations.
     * Resets the download state to Idle when switching languages.
     * Persists the selection to DataStore.
     *
     * @param language The language to select
     */
    fun setLanguage(language: Language) {
        Log.d(TAG, "setLanguage: ${language.code}, persisting to DataStore")
        selectedLanguage = language
        _downloadState.value = DownloadState.Idle

        viewModelScope.launch {
            LanguagePreferences.saveLanguage(dataStore, language.code)
        }
    }

    /**
     * Initiates dictionary download for the selected language.
     *
     * Downloads the dictionary file from the server and inserts words into
     * the local Room database. Progress is reported via [downloadState].
     */
    fun downloadDictionary() {
        viewModelScope.launch {
            _downloadState.value = DownloadState.Downloading

            repository.downloadAndStoreDictionary(selectedLanguage) { wordsInserted ->
                _downloadState.value = DownloadState.Inserting(wordsInserted, selectedLanguage.minWords)
            }
                .onSuccess { _downloadState.value = DownloadState.Success }
                .onFailure { _downloadState.value = DownloadState.Error(it.message ?: "Unknown error") }
        }
    }

    /**
     * Returns a Flow of word count for a specific word length.
     *
     * @param length The word length to count (e.g., 2 for two-letter words)
     * @return Flow that emits the count
     */
    fun getWordCount(length: Int): Flow<Int> {
        return repository.getWordCountByLength(selectedLanguage.code, length)
    }
}

/**
 * Represents the various states of the dictionary download process.
 *
 * This is a sealed class, meaning all possible subclasses are defined here.
 * The compiler can verify exhaustive when-expressions (all cases handled).
 */
sealed class DownloadState {
    /**
     * Initial state before download starts.
     * data object is a singleton - only one instance exists.
     */
    data object Idle : DownloadState()

    /**
     * Downloading the dictionary file from the server.
     * Shows indeterminate progress (spinning indicator).
     */
    data object Downloading : DownloadState()

    /**
     * Inserting words into the database.
     * Shows determinate progress (percentage complete).
     *
     * @property wordsInserted Number of words inserted so far
     * @property expectedWords Expected total words (from Language.minWords)
     */
    data class Inserting(val wordsInserted: Int, val expectedWords: Int) : DownloadState() {
        /**
         * Progress as a float from 0.0 to 1.0.
         * coerceIn ensures the value stays within bounds even if
         * actual word count exceeds the expected minimum.
         */
        val progress: Float
            get() = if (expectedWords > 0) {
                (wordsInserted.toFloat() / expectedWords).coerceIn(0f, 1f)
            } else 0f
    }

    /**
     * Download and insertion completed successfully.
     */
    data object Success : DownloadState()

    /**
     * An error occurred during download or insertion.
     *
     * @property message The error message to display to the user
     */
    data class Error(val message: String) : DownloadState()
}
