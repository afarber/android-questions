package com.wordsbyfarber.ui.viewmodels

// ViewModel for game screens managing static letter grids for Game 1 and Game 2
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun initializeGame(gameType: GameType) {
        val grid = when (gameType) {
            GameType.Game1 -> generateStaticGrid15x15()
            GameType.Game2 -> generateStaticGrid5x5()
        }
        
        _uiState.value = _uiState.value.copy(
            gameType = gameType,
            grid = grid,
            isInitialized = true
        )
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

    private fun generateStaticGrid15x15(): List<List<String>> {
        val letters = listOf(
            "ABCDEFGHIJKLMNO",
            "PQRSTUVWXYZABC",
            "DEFGHIJKLMNOPQ",
            "RSTUVWXYZABCDE",
            "FGHIJKLMNOPQRS",
            "TUVWXYZABCDEFG",
            "HIJKLMNOPQRSTU",
            "VWXYZABCDEFGHI",
            "JKLMNOPQRSTUVW",
            "XYZABCDEFGHIJK",
            "LMNOPQRSTUVWXY",
            "ZABCDEFGHIJKLM",
            "NOPQRSTUVWXYZA",
            "BCDEFGHIJKLMNO",
            "PQRSTUVWXYZABC"
        )
        
        return letters.map { row ->
            row.map { it.toString() }
        }
    }

    private fun generateStaticGrid5x5(): List<List<String>> {
        val letters = listOf(
            "ABCDE",
            "FGHIJ",
            "KLMNO",
            "PQRST",
            "UVWXY"
        )
        
        return letters.map { row ->
            row.map { it.toString() }
        }
    }
}

data class GameUiState(
    val gameType: GameType? = null,
    val grid: List<List<String>> = emptyList(),
    val currentLanguage: String? = null,
    val isInitialized: Boolean = false,
    val shouldNavigateToHome: Boolean = false,
    val error: String? = null
)

enum class GameType {
    Game1,
    Game2
}