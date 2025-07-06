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

class HomeViewModel(
    private val dictionaryRepository: DictionaryRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadCurrentLanguage()
        loadHomeMenuItems()
    }

    private fun loadCurrentLanguage() {
        viewModelScope.launch {
            try {
                val languageCode = preferencesRepository.getLanguage()
                if (languageCode != null) {
                    val languages = dictionaryRepository.getLanguages()
                    val currentLanguage = languages.find { it.code == languageCode }
                    val wordCount = dictionaryRepository.getWordCount(languageCode)
                    
                    _uiState.value = _uiState.value.copy(
                        currentLanguage = currentLanguage,
                        wordCount = wordCount
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        shouldNavigateToLanguageSelection = true
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error loading language"
                )
            }
        }
    }

    private fun loadHomeMenuItems() {
        val menuItems = listOf(
            HomeMenuItem.Game1,
            HomeMenuItem.Game2,
            HomeMenuItem.TopPlayers,
            HomeMenuItem.YourProfile,
            HomeMenuItem.FindWord,
            HomeMenuItem.TwoLetterWords,
            HomeMenuItem.ThreeLetterWords,
            HomeMenuItem.RareLetter1Words,
            HomeMenuItem.RareLetter2Words,
            HomeMenuItem.Preferences,
            HomeMenuItem.Help,
            HomeMenuItem.PrivacyPolicy,
            HomeMenuItem.TermsOfService
        )
        
        _uiState.value = _uiState.value.copy(menuItems = menuItems)
    }

    fun switchLanguage() {
        _uiState.value = _uiState.value.copy(
            shouldNavigateToLanguageSelection = true
        )
    }

    fun selectMenuItem(item: HomeMenuItem) {
        _uiState.value = _uiState.value.copy(
            selectedMenuItem = item,
            shouldNavigateToMenuItem = true
        )
    }

    fun clearNavigationState() {
        _uiState.value = _uiState.value.copy(
            shouldNavigateToLanguageSelection = false,
            shouldNavigateToMenuItem = false,
            selectedMenuItem = null
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class HomeUiState(
    val currentLanguage: Language? = null,
    val wordCount: Int = 0,
    val menuItems: List<HomeMenuItem> = emptyList(),
    val selectedMenuItem: HomeMenuItem? = null,
    val shouldNavigateToLanguageSelection: Boolean = false,
    val shouldNavigateToMenuItem: Boolean = false,
    val error: String? = null
)

sealed class HomeMenuItem(val titleKey: String, val iconName: String) {
    object Game1 : HomeMenuItem("game_1", "grid_15x15")
    object Game2 : HomeMenuItem("game_2", "grid_5x5")
    object TopPlayers : HomeMenuItem("top_players", "leaderboard")
    object YourProfile : HomeMenuItem("your_profile", "person")
    object FindWord : HomeMenuItem("find_word", "search")
    object TwoLetterWords : HomeMenuItem("two_letter_words", "filter_2")
    object ThreeLetterWords : HomeMenuItem("three_letter_words", "filter_3")
    object RareLetter1Words : HomeMenuItem("rare_letter_1_words", "star")
    object RareLetter2Words : HomeMenuItem("rare_letter_2_words", "star_border")
    object Preferences : HomeMenuItem("preferences", "settings")
    object Help : HomeMenuItem("help", "help")
    object PrivacyPolicy : HomeMenuItem("privacy_policy", "privacy_tip")
    object TermsOfService : HomeMenuItem("terms_of_service", "description")
}