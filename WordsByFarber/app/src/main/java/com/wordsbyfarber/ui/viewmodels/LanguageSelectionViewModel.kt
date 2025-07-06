package com.wordsbyfarber.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordsbyfarber.data.models.Language
import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LanguageSelectionViewModel(
    private val dictionaryRepository: DictionaryRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageSelectionUiState())
    val uiState: StateFlow<LanguageSelectionUiState> = _uiState.asStateFlow()

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val languages = dictionaryRepository.getLanguages()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    languages = languages
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun selectLanguage(language: Language) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                preferencesRepository.setLanguage(language.code)
                
                val hasMinWords = dictionaryRepository.hasMinWords(language.code)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedLanguage = language,
                    shouldNavigateToHome = hasMinWords,
                    shouldNavigateToLoading = !hasMinWords
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun clearNavigationState() {
        _uiState.value = _uiState.value.copy(
            shouldNavigateToHome = false,
            shouldNavigateToLoading = false
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class LanguageSelectionUiState(
    val isLoading: Boolean = false,
    val languages: List<Language> = emptyList(),
    val selectedLanguage: Language? = null,
    val shouldNavigateToHome: Boolean = false,
    val shouldNavigateToLoading: Boolean = false,
    val error: String? = null
)