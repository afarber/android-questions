package com.wordsbyfarber.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordsbyfarber.data.database.WordEntity
import com.wordsbyfarber.data.models.Language
import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.PreferencesRepository
import com.wordsbyfarber.utils.StringUtils
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class WordSearchViewModel(
    private val dictionaryRepository: DictionaryRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordSearchUiState())
    val uiState: StateFlow<WordSearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var currentLanguage: Language? = null

    init {
        loadCurrentLanguage()
        setupSearch()
    }

    private fun loadCurrentLanguage() {
        viewModelScope.launch {
            try {
                val languageCode = preferencesRepository.getLanguage()
                if (languageCode == null) {
                    _uiState.value = _uiState.value.copy(
                        shouldNavigateToLanguageSelection = true
                    )
                    return@launch
                }

                val languages = dictionaryRepository.getLanguages()
                currentLanguage = languages.find { it.code == languageCode }
                
                _uiState.value = _uiState.value.copy(
                    currentLanguage = currentLanguage
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error loading language"
                )
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isBlank()) {
                        _uiState.value = _uiState.value.copy(
                            searchResult = null,
                            isSearching = false
                        )
                    } else {
                        searchForWord(query)
                    }
                }
        }
    }

    private fun searchForWord(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            
            try {
                val languageCode = preferencesRepository.getLanguage()
                if (languageCode == null) {
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        shouldNavigateToLanguageSelection = true
                    )
                    return@launch
                }

                val language = currentLanguage
                if (language == null) {
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        error = "Language not found"
                    )
                    return@launch
                }

                // Hash the query word using the same algorithm as the dictionary
                val hashedQuery = StringUtils.hashWord(
                    query.trim().uppercase(),
                    language.rareLetter1,
                    language.rareLetter2
                )

                val foundWord = dictionaryRepository.findWord(languageCode, hashedQuery)
                
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    searchResult = WordSearchResult(
                        originalQuery = query.trim(),
                        word = foundWord,
                        isFound = foundWord != null
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    error = e.message ?: "Error searching for word"
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _uiState.value = _uiState.value.copy(
            searchResult = null,
            isSearching = false
        )
    }

    fun close() {
        _uiState.value = _uiState.value.copy(
            shouldNavigateToHome = true
        )
    }

    fun clearNavigationState() {
        _uiState.value = _uiState.value.copy(
            shouldNavigateToHome = false,
            shouldNavigateToLanguageSelection = false
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class WordSearchUiState(
    val isSearching: Boolean = false,
    val searchResult: WordSearchResult? = null,
    val currentLanguage: Language? = null,
    val shouldNavigateToHome: Boolean = false,
    val shouldNavigateToLanguageSelection: Boolean = false,
    val error: String? = null
)

data class WordSearchResult(
    val originalQuery: String,
    val word: WordEntity?,
    val isFound: Boolean
)