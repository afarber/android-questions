package com.wordsbyfarber.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordsbyfarber.data.database.PlayerEntity
import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val dictionaryRepository: DictionaryRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val languageCode = preferencesRepository.getLanguage()
                if (languageCode == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        shouldNavigateToLanguageSelection = true
                    )
                    return@launch
                }

                // Get my_uid from resources - the default value is 5 as per specs
                val myUid = getMyUidFromResources(languageCode)
                
                val userProfile = dictionaryRepository.getPlayerById(languageCode, myUid)
                
                if (userProfile != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        userProfile = userProfile,
                        currentLanguage = languageCode
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "User profile not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error loading user profile"
                )
            }
        }
    }

    private fun getMyUidFromResources(languageCode: String): Int {
        // This should get my_uid from integers.xml for the specific language
        // For now, returning the default value 5 as mentioned in the specs
        return 5
    }

    fun refreshProfile() {
        loadUserProfile()
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

data class ProfileUiState(
    val isLoading: Boolean = false,
    val userProfile: PlayerEntity? = null,
    val currentLanguage: String? = null,
    val shouldNavigateToHome: Boolean = false,
    val shouldNavigateToLanguageSelection: Boolean = false,
    val error: String? = null
)