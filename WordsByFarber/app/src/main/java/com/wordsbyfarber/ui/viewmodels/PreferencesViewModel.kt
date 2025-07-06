package com.wordsbyfarber.ui.viewmodels

// ViewModel for preferences screen managing app settings and configuration options
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordsbyfarber.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PreferencesViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreferencesUiState())
    val uiState: StateFlow<PreferencesUiState> = _uiState.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            try {
                val currentLanguage = preferencesRepository.getLanguage()
                
                // Create fake preference items as mentioned in the specs
                val preferenceItems = listOf(
                    PreferenceItem(
                        id = "sound_effects",
                        title = "Sound Effects",
                        description = "Enable sound effects during games",
                        isEnabled = true
                    ),
                    PreferenceItem(
                        id = "auto_save",
                        title = "Auto Save",
                        description = "Automatically save game progress",
                        isEnabled = true
                    ),
                    PreferenceItem(
                        id = "dark_theme",
                        title = "Dark Theme",
                        description = "Use dark theme for better visibility",
                        isEnabled = false
                    ),
                    PreferenceItem(
                        id = "notifications",
                        title = "Push Notifications",
                        description = "Receive notifications about new features",
                        isEnabled = true
                    ),
                    PreferenceItem(
                        id = "high_contrast",
                        title = "High Contrast",
                        description = "Increase contrast for better readability",
                        isEnabled = false
                    ),
                    PreferenceItem(
                        id = "large_text",
                        title = "Large Text",
                        description = "Use larger text size",
                        isEnabled = false
                    ),
                    PreferenceItem(
                        id = "vibration",
                        title = "Vibration",
                        description = "Enable vibration feedback",
                        isEnabled = true
                    ),
                    PreferenceItem(
                        id = "analytics",
                        title = "Analytics",
                        description = "Help improve the app by sharing usage data",
                        isEnabled = true
                    ),
                    PreferenceItem(
                        id = "auto_update",
                        title = "Auto Update Dictionary",
                        description = "Automatically update dictionary when available",
                        isEnabled = false
                    ),
                    PreferenceItem(
                        id = "offline_mode",
                        title = "Offline Mode",
                        description = "Enable offline gameplay when possible",
                        isEnabled = true
                    )
                )
                
                _uiState.value = _uiState.value.copy(
                    currentLanguage = currentLanguage,
                    preferenceItems = preferenceItems
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error loading preferences"
                )
            }
        }
    }

    fun togglePreference(preferenceId: String) {
        viewModelScope.launch {
            try {
                val currentItems = _uiState.value.preferenceItems
                val updatedItems = currentItems.map { item ->
                    if (item.id == preferenceId) {
                        item.copy(isEnabled = !item.isEnabled)
                    } else {
                        item
                    }
                }
                
                _uiState.value = _uiState.value.copy(
                    preferenceItems = updatedItems
                )
                
                // Here you would typically save the preference value
                // For now, we'll just update the UI state
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error updating preference"
                )
            }
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            try {
                val defaultItems = _uiState.value.preferenceItems.map { item ->
                    item.copy(isEnabled = getDefaultValueForPreference(item.id))
                }
                
                _uiState.value = _uiState.value.copy(
                    preferenceItems = defaultItems
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error resetting preferences"
                )
            }
        }
    }

    private fun getDefaultValueForPreference(preferenceId: String): Boolean {
        return when (preferenceId) {
            "sound_effects" -> true
            "auto_save" -> true
            "dark_theme" -> false
            "notifications" -> true
            "high_contrast" -> false
            "large_text" -> false
            "vibration" -> true
            "analytics" -> true
            "auto_update" -> false
            "offline_mode" -> true
            else -> false
        }
    }

    fun close() {
        _uiState.value = _uiState.value.copy(
            shouldNavigateToHome = true
        )
    }

    fun clearNavigationState() {
        _uiState.value = _uiState.value.copy(
            shouldNavigateToHome = false
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class PreferencesUiState(
    val currentLanguage: String? = null,
    val preferenceItems: List<PreferenceItem> = emptyList(),
    val shouldNavigateToHome: Boolean = false,
    val error: String? = null
)

data class PreferenceItem(
    val id: String,
    val title: String,
    val description: String,
    val isEnabled: Boolean
)