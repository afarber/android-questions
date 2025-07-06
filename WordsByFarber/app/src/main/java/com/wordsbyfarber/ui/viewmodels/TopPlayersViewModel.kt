package com.wordsbyfarber.ui.viewmodels

// ViewModel for top players screen managing player loading and search functionality
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordsbyfarber.data.database.PlayerEntity
import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.PreferencesRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class TopPlayersViewModel(
    private val dictionaryRepository: DictionaryRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TopPlayersUiState())
    val uiState: StateFlow<TopPlayersUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadPlayers()
        setupSearch()
    }

    @OptIn(FlowPreview::class)
    private fun setupSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .combine(_uiState) { query, state ->
                    if (query.isEmpty()) {
                        state.allPlayers
                    } else {
                        state.allPlayers.filter { player ->
                            player.given.contains(query, ignoreCase = true) ||
                            player.motto?.contains(query, ignoreCase = true) == true
                        }
                    }
                }
                .collect { filteredPlayers ->
                    _uiState.value = _uiState.value.copy(
                        filteredPlayers = filteredPlayers.sortedByDescending { it.elo }
                    )
                }
        }
    }

    fun loadPlayers() {
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

                dictionaryRepository.getAllPlayers(languageCode)
                    .collect { players ->
                        val sortedPlayers = players.sortedByDescending { it.elo }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            allPlayers = sortedPlayers,
                            filteredPlayers = sortedPlayers
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error loading players"
                )
            }
        }
    }

    fun downloadAndRefreshPlayers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            
            try {
                val languageCode = preferencesRepository.getLanguage()
                if (languageCode == null) {
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        shouldNavigateToLanguageSelection = true
                    )
                    return@launch
                }

                // Clear existing players
                dictionaryRepository.clearPlayersTable(languageCode)
                
                // Download new players from top_url
                // This would need to be implemented with actual network call
                // For now, we'll just refresh the existing data
                loadPlayers()
                
                _uiState.value = _uiState.value.copy(isRefreshing = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    error = e.message ?: "Error refreshing players"
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
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

data class TopPlayersUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val allPlayers: List<PlayerEntity> = emptyList(),
    val filteredPlayers: List<PlayerEntity> = emptyList(),
    val shouldNavigateToHome: Boolean = false,
    val shouldNavigateToLanguageSelection: Boolean = false,
    val error: String? = null
)