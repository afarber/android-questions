package com.wordsbyfarber.ui.screens

// Screen 1: Language selection screen displaying list of supported languages
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.wordsbyfarber.data.models.Language
import com.wordsbyfarber.ui.components.LanguageListItem
import com.wordsbyfarber.ui.components.LoadingIndicator
import com.wordsbyfarber.ui.theme.WordsByFarberTheme
import com.wordsbyfarber.ui.viewmodels.LanguageSelectionUiState
import com.wordsbyfarber.ui.viewmodels.LanguageSelectionViewModel

@Composable
fun LanguageSelectionScreen(
    viewModel: LanguageSelectionViewModel,
    onLanguageSelected: (Language) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle navigation
    LaunchedEffect(uiState.shouldNavigateToLoading, uiState.shouldNavigateToHome) {
        if (uiState.shouldNavigateToLoading || uiState.shouldNavigateToHome) {
            uiState.selectedLanguage?.let { language ->
                onLanguageSelected(language)
                viewModel.clearNavigationState()
            }
        }
    }

    // Handle errors
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        LanguageSelectionContent(
            uiState = uiState,
            onLanguageSelected = { language ->
                viewModel.selectLanguage(language)
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun LanguageSelectionContent(
    uiState: LanguageSelectionUiState,
    onLanguageSelected: (Language) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            uiState.isLoading -> {
                LoadingIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            
            uiState.languages.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.languages) { language ->
                        LanguageListItem(
                            language = language,
                            onClick = { onLanguageSelected(language) }
                        )
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
                        code = "de",
                        name = "Deutsch (de)",
                        rareLetter1 = "Q",
                        rareLetter2 = "Y",
                        hashedDictionaryUrl = "",
                        topUrl = "",
                        minWords = 180000,
                        myUid = 5
                    )
                )
            ),
            onLanguageSelected = { }
        )
    }
}