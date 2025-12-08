package com.wordsbyfarber.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordsbyfarber.data.model.Language
import com.wordsbyfarber.data.repository.DictionaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/**
 * ViewModel that manages dictionary download and data access.
 *
 * This ViewModel survives configuration changes (like screen rotation) and
 * provides a single source of truth for the download state. It uses Koin
 * for dependency injection of the repository.
 *
 * @param repository The repository that handles network and database operations
 */
class DictionaryViewModel(
    private val repository: DictionaryRepository
) : ViewModel() {

    /**
     * The currently selected language for dictionary operations.
     * Null if no language has been selected yet.
     * Private setter ensures only this ViewModel can modify it.
     */
    var selectedLanguage: Language? = null
        private set

    // Backing property pattern: _downloadState is mutable internally,
    // downloadState is read-only externally
    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)

    /**
     * Observable state of the download process.
     * UI observes this to update progress indicators and navigate on completion.
     */
    val downloadState: StateFlow<DownloadState> = _downloadState

    /**
     * Sets the language for subsequent dictionary operations.
     * Resets the download state to Idle when switching languages.
     *
     * @param language The language to select
     */
    fun selectLanguage(language: Language) {
        selectedLanguage = language
        _downloadState.value = DownloadState.Idle
    }

    /**
     * Initiates dictionary download for the selected language.
     *
     * Downloads the dictionary file from the server and inserts words into
     * the local Room database. Progress is reported via [downloadState].
     *
     * Does nothing if no language is selected (early return with ?: operator).
     */
    fun downloadDictionary() {
        // Elvis operator: if selectedLanguage is null, return early
        val language = selectedLanguage ?: return

        // viewModelScope automatically cancels coroutines when ViewModel is cleared
        viewModelScope.launch {
            _downloadState.value = DownloadState.Downloading

            // Pass a lambda to receive progress updates during batch inserts
            repository.downloadAndStoreDictionary(language) { wordsInserted ->
                _downloadState.value = DownloadState.Inserting(wordsInserted, language.minWords)
            }
                // Result.onSuccess/onFailure pattern for handling the result
                .onSuccess { _downloadState.value = DownloadState.Success }
                .onFailure { _downloadState.value = DownloadState.Error(it.message ?: "Unknown error") }
        }
    }

    /**
     * Returns a Flow of word count for a specific word length.
     *
     * @param length The word length to count (e.g., 2 for two-letter words)
     * @return Flow that emits the count, or flowOf(0) if no language selected
     */
    fun getWordCount(length: Int): Flow<Int> {
        val code = selectedLanguage?.code ?: return flowOf(0)
        return repository.getWordCountByLength(code, length)
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
