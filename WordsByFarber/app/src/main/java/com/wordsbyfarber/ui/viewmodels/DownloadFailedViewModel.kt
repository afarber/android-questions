package com.wordsbyfarber.ui.viewmodels

// ViewModel for download failed screen handling retry and cancellation actions
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DownloadFailedViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DownloadFailedUiState())
    val uiState: StateFlow<DownloadFailedUiState> = _uiState.asStateFlow()

    fun retry() {
        _uiState.value = _uiState.value.copy(
            isRetrying = true,
            shouldNavigateToLoading = true,
            shouldNavigateToLanguageSelection = false
        )
    }

    fun close() {
        viewModelScope.launch {
            preferencesRepository.clearLanguage()
            _uiState.value = _uiState.value.copy(
                shouldNavigateToLanguageSelection = true
            )
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

    class Factory(
        private val preferencesRepository: PreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DownloadFailedViewModel::class.java)) {
                return DownloadFailedViewModel(preferencesRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class DownloadFailedUiState(
    val isRetrying: Boolean = false,
    val shouldNavigateToLoading: Boolean = false,
    val shouldNavigateToLanguageSelection: Boolean = false,
    val error: String? = null
)