package com.wordsbyfarber.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordsbyfarber.data.database.WordEntity
import com.wordsbyfarber.data.models.Language
import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordListViewModel @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordListUiState())
    val uiState: StateFlow<WordListUiState> = _uiState.asStateFlow()

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
                .combine(_uiState) { query, state ->
                    if (query.isEmpty()) {
                        state.allWords
                    } else {
                        state.allWords.filter { word ->
                            word.word.contains(query, ignoreCase = true) ||
                            word.explanation?.contains(query, ignoreCase = true) == true
                        }
                    }
                }
                .collect { filteredWords ->
                    _uiState.value = _uiState.value.copy(
                        filteredWords = filteredWords
                    )
                }
        }
    }

    fun loadWords(wordType: WordType) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                wordType = wordType
            )
            
            try {
                val languageCode = preferencesRepository.getLanguage()
                if (languageCode == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        shouldNavigateToLanguageSelection = true
                    )
                    return@launch
                }

                val language = currentLanguage
                if (language == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Language not found"
                    )
                    return@launch
                }

                when (wordType) {
                    WordType.TwoLetter -> {
                        dictionaryRepository.getWordsByLength(languageCode, 2)
                            .collect { words ->
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    allWords = words,
                                    filteredWords = words
                                )
                            }
                    }
                    WordType.ThreeLetter -> {
                        dictionaryRepository.getWordsByLength(languageCode, 3)
                            .collect { words ->
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    allWords = words,
                                    filteredWords = words
                                )
                            }
                    }
                    WordType.RareLetter1 -> {
                        dictionaryRepository.getWordsByRareLetter(languageCode, language.rareLetter1)
                            .collect { words ->
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    allWords = words,
                                    filteredWords = words
                                )
                            }
                    }
                    WordType.RareLetter2 -> {
                        dictionaryRepository.getWordsByRareLetter(languageCode, language.rareLetter2)
                            .collect { words ->
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    allWords = words,
                                    filteredWords = words
                                )
                            }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error loading words"
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
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

    fun getScreenTitle(): String {
        return when (_uiState.value.wordType) {
            WordType.TwoLetter -> "2-letter words"
            WordType.ThreeLetter -> "3-letter words"
            WordType.RareLetter1 -> "Words with ${currentLanguage?.rareLetter1 ?: "rare letter"}"
            WordType.RareLetter2 -> "Words with ${currentLanguage?.rareLetter2 ?: "rare letter"}"
            null -> "Words"
        }
    }
}

data class WordListUiState(
    val isLoading: Boolean = false,
    val wordType: WordType? = null,
    val allWords: List<WordEntity> = emptyList(),
    val filteredWords: List<WordEntity> = emptyList(),
    val currentLanguage: Language? = null,
    val shouldNavigateToHome: Boolean = false,
    val shouldNavigateToLanguageSelection: Boolean = false,
    val error: String? = null
)

enum class WordType {
    TwoLetter,
    ThreeLetter,
    RareLetter1,
    RareLetter2
}