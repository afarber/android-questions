package com.wordsbyfarber.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordsbyfarber.ui.components.AppTopBar
import com.wordsbyfarber.ui.components.LoadingIndicator
import com.wordsbyfarber.ui.theme.WordsByFarberTheme
import com.wordsbyfarber.ui.viewmodels.LoadingDictionaryUiState
import com.wordsbyfarber.ui.viewmodels.LoadingDictionaryViewModel

@Composable
fun LoadingDictionaryScreen(
    languageCode: String,
    onNavigateToHome: () -> Unit,
    onNavigateToError: () -> Unit,
    onNavigateToLanguageSelection: () -> Unit,
    viewModel: LoadingDictionaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(languageCode) {
        viewModel.startDownload(languageCode)
    }

    LaunchedEffect(uiState.shouldNavigateToHome) {
        if (uiState.shouldNavigateToHome) {
            viewModel.clearNavigationState()
            onNavigateToHome()
        }
    }

    LaunchedEffect(uiState.shouldNavigateToError) {
        if (uiState.shouldNavigateToError) {
            viewModel.clearNavigationState()
            onNavigateToError()
        }
    }

    LaunchedEffect(uiState.shouldNavigateToLanguageSelection) {
        if (uiState.shouldNavigateToLanguageSelection) {
            viewModel.clearNavigationState()
            onNavigateToLanguageSelection()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    LoadingDictionaryContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onCancelClick = {
            viewModel.cancelDownload()
        }
    )
}

@Composable
private fun LoadingDictionaryContent(
    uiState: LoadingDictionaryUiState,
    snackbarHostState: SnackbarHostState,
    onCancelClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Loading Dictionary",
                onCloseClick = onCancelClick
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val progress = if (uiState.progress > 0) uiState.progress / 100f else null
                val message = when {
                    uiState.statusMessage.isNotBlank() -> uiState.statusMessage
                    uiState.isLoading -> "Loading dictionary..."
                    else -> null
                }

                LoadingIndicator(
                    message = message,
                    progress = progress,
                    currentProgress = if (uiState.wordCount > 0) uiState.wordCount else null,
                    maxProgress = if (uiState.wordCount > 0 && progress != null) {
                        (uiState.wordCount / progress).toInt()
                    } else null
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingDictionaryScreenPreview() {
    WordsByFarberTheme {
        LoadingDictionaryContent(
            uiState = LoadingDictionaryUiState(
                isLoading = true,
                progress = 0,
                statusMessage = "Downloading dictionary...",
                wordCount = 0
            ),
            snackbarHostState = SnackbarHostState(),
            onCancelClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingDictionaryScreenProgressPreview() {
    WordsByFarberTheme {
        LoadingDictionaryContent(
            uiState = LoadingDictionaryUiState(
                isLoading = true,
                progress = 65,
                statusMessage = "Parsing dictionary...",
                wordCount = 65000
            ),
            snackbarHostState = SnackbarHostState(),
            onCancelClick = {}
        )
    }
}