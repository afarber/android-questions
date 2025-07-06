package com.wordsbyfarber.ui.viewmodels

// ViewModel for dictionary loading screen managing download and parsing operations
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.DictionaryDownloadState
import com.wordsbyfarber.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoadingDictionaryViewModel(
    private val dictionaryRepository: DictionaryRepository,
    private val preferencesRepository: PreferencesRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoadingDictionaryUiState())
    val uiState: StateFlow<LoadingDictionaryUiState> = _uiState.asStateFlow()

    private var downloadJob: kotlinx.coroutines.Job? = null

    fun startDownload(languageCode: String) {
        downloadJob?.cancel()
        downloadJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                progress = 0,
                error = null,
                shouldNavigateToHome = false,
                shouldNavigateToError = false
            )
            
            try {
                dictionaryRepository.clearWordsTable(languageCode)
                
                dictionaryRepository.downloadAndParseDictionary(languageCode)
                    .collect { state ->
                        when (state) {
                            is DictionaryDownloadState.Downloading -> {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = true,
                                    progress = state.progress,
                                    statusMessage = "Downloading dictionary..."
                                )
                            }
                            is DictionaryDownloadState.Parsing -> {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = true,
                                    progress = state.progress,
                                    statusMessage = "Parsing dictionary..."
                                )
                            }
                            is DictionaryDownloadState.Success -> {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    progress = 100,
                                    statusMessage = "Dictionary loaded successfully",
                                    shouldNavigateToHome = true,
                                    wordCount = state.wordCount
                                )
                            }
                            is DictionaryDownloadState.Error -> {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    error = state.message,
                                    shouldNavigateToError = true
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred",
                    shouldNavigateToError = true
                )
            }
        }
    }

    fun cancelDownload() {
        downloadJob?.cancel()
        viewModelScope.launch {
            try {
                val languageCode = preferencesRepository.getLanguage()
                if (languageCode != null) {
                    dictionaryRepository.clearWordsTable(languageCode)
                }
                preferencesRepository.clearLanguage()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    shouldNavigateToLanguageSelection = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error during cancellation"
                )
            }
        }
    }

    fun clearNavigationState() {
        _uiState.value = _uiState.value.copy(
            shouldNavigateToHome = false,
            shouldNavigateToError = false,
            shouldNavigateToLanguageSelection = false
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    init {
        // Start download automatically when ViewModel is created
        val languageCode = preferencesRepository.getCurrentLanguage()
        if (languageCode != null) {
            startDownload(languageCode)
        }
    }

    class Factory(
        private val preferencesRepository: PreferencesRepository,
        private val dictionaryRepository: DictionaryRepository,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoadingDictionaryViewModel::class.java)) {
                return LoadingDictionaryViewModel(dictionaryRepository, preferencesRepository, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class LoadingDictionaryUiState(
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val statusMessage: String = "",
    val wordCount: Int = 0,
    val shouldNavigateToHome: Boolean = false,
    val shouldNavigateToError: Boolean = false,
    val shouldNavigateToLanguageSelection: Boolean = false,
    val error: String? = null
)