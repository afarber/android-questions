package com.wordsbyfarber.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.wordsbyfarber.data.models.Language
import com.wordsbyfarber.ui.components.LanguageListItem
import com.wordsbyfarber.ui.components.LoadingIndicator
import com.wordsbyfarber.ui.theme.WordsByFarberTheme
import com.wordsbyfarber.ui.viewmodels.LanguageSelectionUiState
import com.wordsbyfarber.ui.viewmodels.LanguageSelectionViewModel

@Composable
fun LanguageSelectionScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLoading: () -> Unit,
    viewModel: LanguageSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.shouldNavigateToHome) {
        if (uiState.shouldNavigateToHome) {
            viewModel.clearNavigationState()
            onNavigateToHome()
        }
    }

    LaunchedEffect(uiState.shouldNavigateToLoading) {
        if (uiState.shouldNavigateToLoading) {
            viewModel.clearNavigationState()
            onNavigateToLoading()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    LanguageSelectionContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onLanguageSelect = { language ->
            viewModel.selectLanguage(language)
        }
    )
}

@Composable
private fun LanguageSelectionContent(
    uiState: LanguageSelectionUiState,
    snackbarHostState: SnackbarHostState,
    onLanguageSelect: (Language) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator(
                        message = "Loading languages...",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                uiState.languages.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.languages,
                            key = { it.code }
                        ) { language ->
                            LanguageListItem(
                                language = language,
                                onClick = { onLanguageSelect(language) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageSelectionScreenPreview() {
    WordsByFarberTheme {
        LanguageSelectionContent(
            uiState = LanguageSelectionUiState(
                isLoading = false,
                languages = listOf(
                    Language(
                        code = "de",
                        name = "Deutsch (de)",
                        rareLetter1 = "Q",
                        rareLetter2 = "Y",
                        hashedDictionaryUrl = "",
                        topUrl = "",
                        minWords = 180000,
                        myUid = 5
                    ),
                    Language(
                        code = "en",
                        name = "English (en)",
                        rareLetter1 = "Q",
                        rareLetter2 = "X",
                        hashedDictionaryUrl = "",
                        topUrl = "",
                        minWords = 270000,
                        myUid = 5
                    ),
                    Language(
                        code = "fr",
                        name = "Français (fr)",
                        rareLetter1 = "K",
                        rareLetter2 = "W",
                        hashedDictionaryUrl = "",
                        topUrl = "",
                        minWords = 370000,
                        myUid = 5
                    )
                )
            ),
            snackbarHostState = SnackbarHostState(),
            onLanguageSelect = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageSelectionScreenLoadingPreview() {
    WordsByFarberTheme {
        LanguageSelectionContent(
            uiState = LanguageSelectionUiState(
                isLoading = true,
                languages = emptyList()
            ),
            snackbarHostState = SnackbarHostState(),
            onLanguageSelect = {}
        )
    }
}