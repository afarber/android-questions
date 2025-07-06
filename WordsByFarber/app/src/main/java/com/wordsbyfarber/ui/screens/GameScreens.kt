package com.wordsbyfarber.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordsbyfarber.ui.components.AppTopBar
import com.wordsbyfarber.ui.components.LetterGrid
import com.wordsbyfarber.ui.components.LoadingIndicator
import com.wordsbyfarber.ui.components.StaticLetterGrid15x15
import com.wordsbyfarber.ui.components.StaticLetterGrid5x5
import com.wordsbyfarber.ui.theme.WordsByFarberTheme
import com.wordsbyfarber.ui.viewmodels.GameType
import com.wordsbyfarber.ui.viewmodels.GameUiState
import com.wordsbyfarber.ui.viewmodels.GameViewModel

@Composable
fun Game1Screen(
    onNavigateToHome: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.initializeGame(GameType.Game1)
    }

    LaunchedEffect(uiState.shouldNavigateToHome) {
        if (uiState.shouldNavigateToHome) {
            viewModel.clearNavigationState()
            onNavigateToHome()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    GameContent(
        title = "Game 1",
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onCloseClick = {
            viewModel.close()
        }
    )
}

@Composable
fun Game2Screen(
    onNavigateToHome: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.initializeGame(GameType.Game2)
    }

    LaunchedEffect(uiState.shouldNavigateToHome) {
        if (uiState.shouldNavigateToHome) {
            viewModel.clearNavigationState()
            onNavigateToHome()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    GameContent(
        title = "Game 2",
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onCloseClick = {
            viewModel.close()
        }
    )
}

@Composable
private fun GameContent(
    title: String,
    uiState: GameUiState,
    snackbarHostState: SnackbarHostState,
    onCloseClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = title,
                onCloseClick = onCloseClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                !uiState.isInitialized -> {
                    LoadingIndicator(
                        message = "Initializing game..."
                    )
                }
                
                uiState.grid.isNotEmpty() -> {
                    LetterGrid(
                        grid = uiState.grid,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Game1ScreenPreview() {
    WordsByFarberTheme {
        GameContent(
            title = "Game 1",
            uiState = GameUiState(
                gameType = GameType.Game1,
                grid = listOf(
                    listOf("A", "B", "C", "D", "E"),
                    listOf("F", "G", "H", "I", "J"),
                    listOf("K", "L", "M", "N", "O"),
                    listOf("P", "Q", "R", "S", "T"),
                    listOf("U", "V", "W", "X", "Y")
                ),
                currentLanguage = "en",
                isInitialized = true
            ),
            snackbarHostState = SnackbarHostState(),
            onCloseClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Game2ScreenPreview() {
    WordsByFarberTheme {
        GameContent(
            title = "Game 2",
            uiState = GameUiState(
                gameType = GameType.Game2,
                grid = listOf(
                    listOf("A", "B", "C", "D", "E"),
                    listOf("F", "G", "H", "I", "J"),
                    listOf("K", "L", "M", "N", "O"),
                    listOf("P", "Q", "R", "S", "T"),
                    listOf("U", "V", "W", "X", "Y")
                ),
                currentLanguage = "en",
                isInitialized = true
            ),
            snackbarHostState = SnackbarHostState(),
            onCloseClick = {}
        )
    }
}