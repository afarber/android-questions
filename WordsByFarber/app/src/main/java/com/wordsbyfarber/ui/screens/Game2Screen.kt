package com.wordsbyfarber.ui.screens

// Screen 6: Game 2 screen displaying static 5x5 letter grid
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.wordsbyfarber.R
import com.wordsbyfarber.ui.components.AppTopBar
import com.wordsbyfarber.ui.components.LetterGrid
import com.wordsbyfarber.ui.components.LoadingIndicator
import com.wordsbyfarber.ui.theme.WordsByFarberTheme
import com.wordsbyfarber.ui.viewmodels.GameViewModel
import com.wordsbyfarber.ui.viewmodels.GameType
import com.wordsbyfarber.ui.viewmodels.GameUiState

@Composable
fun Game2Screen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Initialize game when screen opens
    LaunchedEffect(Unit) {
        if (!uiState.isInitialized) {
            viewModel.initializeGame(GameType.Game2)
        }
    }

    // Handle navigation
    LaunchedEffect(uiState.shouldNavigateToHome) {
        if (uiState.shouldNavigateToHome) {
            onNavigateBack()
            viewModel.clearNavigationState()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.game_2),
                onCloseClick = { viewModel.close() }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Game2Content(
            uiState = uiState,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun Game2Content(
    uiState: GameUiState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            !uiState.isInitialized || uiState.grid.isEmpty() -> {
                LoadingIndicator()
            }
            
            uiState.grid.isNotEmpty() -> {
                LetterGrid(
                    grid = uiState.grid
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Game2ScreenPreview() {
    WordsByFarberTheme {
        Game2Content(
            uiState = GameUiState(
                gameType = GameType.Game2,
                grid = List(5) { List(5) { "A" } },
                isInitialized = true
            )
        )
    }
}