package com.wordsbyfarber.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordsbyfarber.data.database.WordEntity
import com.wordsbyfarber.ui.components.AppTopBar
import com.wordsbyfarber.ui.components.LoadingIndicator
import com.wordsbyfarber.ui.components.SearchField
import com.wordsbyfarber.ui.components.WordListItem
import com.wordsbyfarber.ui.theme.WordsByFarberTheme
import com.wordsbyfarber.ui.viewmodels.WordListUiState
import com.wordsbyfarber.ui.viewmodels.WordListViewModel
import com.wordsbyfarber.ui.viewmodels.WordType

@Composable
fun TwoLetterWordsScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLanguageSelection: () -> Unit,
    viewModel: WordListViewModel = hiltViewModel()
) {
    WordListScreen(
        wordType = WordType.TwoLetter,
        onNavigateToHome = onNavigateToHome,
        onNavigateToLanguageSelection = onNavigateToLanguageSelection,
        viewModel = viewModel
    )
}

@Composable
fun ThreeLetterWordsScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLanguageSelection: () -> Unit,
    viewModel: WordListViewModel = hiltViewModel()
) {
    WordListScreen(
        wordType = WordType.ThreeLetter,
        onNavigateToHome = onNavigateToHome,
        onNavigateToLanguageSelection = onNavigateToLanguageSelection,
        viewModel = viewModel
    )
}

@Composable
fun RareLetter1WordsScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLanguageSelection: () -> Unit,
    viewModel: WordListViewModel = hiltViewModel()
) {
    WordListScreen(
        wordType = WordType.RareLetter1,
        onNavigateToHome = onNavigateToHome,
        onNavigateToLanguageSelection = onNavigateToLanguageSelection,
        viewModel = viewModel
    )
}

@Composable
fun RareLetter2WordsScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLanguageSelection: () -> Unit,
    viewModel: WordListViewModel = hiltViewModel()
) {
    WordListScreen(
        wordType = WordType.RareLetter2,
        onNavigateToHome = onNavigateToHome,
        onNavigateToLanguageSelection = onNavigateToLanguageSelection,
        viewModel = viewModel
    )
}

@Composable
private fun WordListScreen(
    wordType: WordType,
    onNavigateToHome: () -> Unit,
    onNavigateToLanguageSelection: () -> Unit,
    viewModel: WordListViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(wordType) {
        viewModel.loadWords(wordType)
    }

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

    WordListContent(
        title = viewModel.getScreenTitle(),
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
private fun WordListContent(
    title: String,
    uiState: WordListUiState,
    searchQuery: String,
    snackbarHostState: SnackbarHostState,
    onSearchQueryChange: (String) -> Unit,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search field
            SearchField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = "Search words...",
                modifier = Modifier.padding(16.dp)
            )
            
            // Words list
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    uiState.isLoading -> {
                        LoadingIndicator(
                            message = "Loading words...",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    
                    uiState.filteredWords.isEmpty() && searchQuery.isNotBlank() -> {
                        Text(
                            text = "No words found matching '$searchQuery'",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp)
                        )
                    }
                    
                    uiState.filteredWords.isEmpty() -> {
                        Text(
                            text = "No words available",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp)
                        )
                    }
                    
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = uiState.filteredWords,
                                key = { it.word }
                            ) { word ->
                                WordListItem(word = word)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WordListScreenPreview() {
    WordsByFarberTheme {
        WordListContent(
            title = "2-letter words",
            uiState = WordListUiState(
                isLoading = false,
                wordType = WordType.TwoLetter,
                allWords = listOf(
                    WordEntity("AM", "First person singular present of be"),
                    WordEntity("AN", "The form of the indefinite article"),
                    WordEntity("AS", "Used in comparisons to refer to extent or degree"),
                    WordEntity("AT", "Expressing location or arrival"),
                    WordEntity("BE", "Exist; have reality")
                ),
                filteredWords = listOf(
                    WordEntity("AM", "First person singular present of be"),
                    WordEntity("AN", "The form of the indefinite article"),
                    WordEntity("AS", "Used in comparisons to refer to extent or degree"),
                    WordEntity("AT", "Expressing location or arrival"),
                    WordEntity("BE", "Exist; have reality")
                )
            ),
            searchQuery = "",
            snackbarHostState = SnackbarHostState(),
            onSearchQueryChange = {},
            onCloseClick = {}
        )
    }
}