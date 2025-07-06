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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordsbyfarber.data.database.WordEntity
import com.wordsbyfarber.ui.components.AppTopBar
import com.wordsbyfarber.ui.components.SearchField
import com.wordsbyfarber.ui.components.SmallLoadingIndicator
import com.wordsbyfarber.ui.components.WordFoundIndicator
import com.wordsbyfarber.ui.theme.WordsByFarberTheme
import com.wordsbyfarber.ui.viewmodels.WordSearchResult
import com.wordsbyfarber.ui.viewmodels.WordSearchUiState
import com.wordsbyfarber.ui.viewmodels.WordSearchViewModel

@Composable
fun FindWordScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLanguageSelection: () -> Unit,
    viewModel: WordSearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.shouldNavigateToHome) {
        if (uiState.shouldNavigateToHome) {
            viewModel.clearNavigationState()
            onNavigateToHome()
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

    FindWordContent(
        uiState = uiState,
        searchQuery = searchQuery,
        snackbarHostState = snackbarHostState,
        onSearchQueryChange = { query ->
            viewModel.updateSearchQuery(query)
        },
        onCloseClick = {
            viewModel.close()
        }
    )
}

@Composable
private fun FindWordContent(
    uiState: WordSearchUiState,
    searchQuery: String,
    snackbarHostState: SnackbarHostState,
    onSearchQueryChange: (String) -> Unit,
    onCloseClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Find a Word",
                onCloseClick = onCloseClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search field
            SearchField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = "Enter a word to search...",
                modifier = Modifier.padding(16.dp)
            )
            
            // Result area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isSearching -> {
                        SmallLoadingIndicator()
                    }
                    
                    uiState.searchResult != null -> {
                        WordFoundIndicator(
                            isFound = uiState.searchResult.isFound,
                            explanation = uiState.searchResult.word?.explanation
                        )
                    }
                    
                    searchQuery.isBlank() -> {
                        // Show nothing when search is empty
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FindWordScreenPreview() {
    WordsByFarberTheme {
        FindWordContent(
            uiState = WordSearchUiState(
                isSearching = false,
                searchResult = WordSearchResult(
                    originalQuery = "EXAMPLE",
                    word = WordEntity(
                        word = "EXAMPLE",
                        explanation = "A thing characteristic of its kind or illustrating a general rule"
                    ),
                    isFound = true
                )
            ),
            searchQuery = "example",
            snackbarHostState = SnackbarHostState(),
            onSearchQueryChange = {},
            onCloseClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FindWordScreenNotFoundPreview() {
    WordsByFarberTheme {
        FindWordContent(
            uiState = WordSearchUiState(
                isSearching = false,
                searchResult = WordSearchResult(
                    originalQuery = "NOTFOUND",
                    word = null,
                    isFound = false
                )
            ),
            searchQuery = "notfound",
            snackbarHostState = SnackbarHostState(),
            onSearchQueryChange = {},
            onCloseClick = {}
        )
    }
}