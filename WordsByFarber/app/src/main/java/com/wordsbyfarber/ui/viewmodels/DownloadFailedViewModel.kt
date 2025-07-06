package com.wordsbyfarber.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadFailedViewModel @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DownloadFailedUiState())
    val uiState: StateFlow<DownloadFailedUiState> = _uiState.asStateFlow()

    fun retry() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isRetrying = true,
                shouldNavigateToLoading = false,
                shouldNavigateToLanguageSelection = false
            )
            
            try {
                val languageCode = preferencesRepository.getLanguage()
                if (languageCode != null) {
                    dictionaryRepository.clearWordsTable(languageCode)
                    _uiState.value = _uiState.value.copy(
                        isRetrying = false,
                        shouldNavigateToLoading = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isRetrying = false,
                        shouldNavigateToLanguageSelection = true
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRetrying = false,
                    error = e.message ?: "Error during retry"
                )
            }
        }
    }

    fun close() {
        viewModelScope.launch {
            try {
                val languageCode = preferencesRepository.getLanguage()
                if (languageCode != null) {
                    dictionaryRepository.clearWordsTable(languageCode)
                }
                preferencesRepository.clearLanguage()
                
                _uiState.value = _uiState.value.copy(
                    shouldNavigateToLanguageSelection = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error during close"
                )
            }
        }
    }

    fun clearNavigationState() {
        _uiState.value = _uiState.value.copy(
            shouldNavigateToLoading = false,
            shouldNavigateToLanguageSelection = false
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class DownloadFailedUiState(
    val isRetrying: Boolean = false,
    val shouldNavigateToLoading: Boolean = false,
    val shouldNavigateToLanguageSelection: Boolean = false,
    val error: String? = null
)