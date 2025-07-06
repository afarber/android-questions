package com.wordsbyfarber.ui.viewmodels

// ViewModel for content screens managing help, privacy policy, and terms of service
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordsbyfarber.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContentViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContentUiState())
    val uiState: StateFlow<ContentUiState> = _uiState.asStateFlow()

    fun loadContent(contentType: ContentType) {
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

                // Load content based on type
                val content = when (contentType) {
                    ContentType.Help -> getHelpContent(languageCode)
                    ContentType.PrivacyPolicy -> getPrivacyPolicyContent(languageCode)
                    ContentType.TermsOfService -> getTermsOfServiceContent(languageCode)
                }

                val title = when (contentType) {
                    ContentType.Help -> "Help"
                    ContentType.PrivacyPolicy -> "Privacy Policy"
                    ContentType.TermsOfService -> "Terms of Service"
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    contentType = contentType,
                    title = title,
                    content = content,
                    currentLanguage = languageCode
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error loading content"
                )
            }
        }
    }

    private fun getHelpContent(languageCode: String): String {
        // In a real app, this would load from strings.xml resources
        // For now, returning fake lorem ipsum content as specified
        return when (languageCode) {
            "de" -> """
                # Hilfe

                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris.

                ## Spielregeln
                
                Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

                ## Häufige Fragen
                
                Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis.
            """.trimIndent()
            
            "en" -> """
                # Help

                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris.

                ## Game Rules
                
                Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

                ## Frequently Asked Questions
                
                Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis.
            """.trimIndent()
            
            else -> """
                # Help

                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris.

                ## Game Rules
                
                Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

                ## Frequently Asked Questions
                
                Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis.
            """.trimIndent()
        }
    }

    private fun getPrivacyPolicyContent(languageCode: String): String {
        return """
            # Privacy Policy

            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.

            ## Data Collection
            
            Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

            ## Data Usage
            
            Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.

            ## Data Protection
            
            Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt.

            ## Contact Information
            
            At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident.
        """.trimIndent()
    }

    private fun getTermsOfServiceContent(languageCode: String): String {
        return """
            # Terms of Service

            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.

            ## Acceptance of Terms
            
            Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

            ## User Responsibilities
            
            Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.

            ## Service Availability
            
            Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt.

            ## Limitation of Liability
            
            At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident.

            ## Modifications
            
            Similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio.
        """.trimIndent()
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

data class ContentUiState(
    val isLoading: Boolean = false,
    val contentType: ContentType? = null,
    val title: String = "",
    val content: String = "",
    val currentLanguage: String? = null,
    val shouldNavigateToHome: Boolean = false,
    val shouldNavigateToLanguageSelection: Boolean = false,
    val error: String? = null
)

enum class ContentType {
    Help,
    PrivacyPolicy,
    TermsOfService
}