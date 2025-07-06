package com.wordsbyfarber.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordsbyfarber.ui.components.HomeMenuItem
import com.wordsbyfarber.ui.components.HomeTopBar
import com.wordsbyfarber.ui.components.LoadingIndicator
import com.wordsbyfarber.ui.theme.WordsByFarberTheme
import com.wordsbyfarber.ui.viewmodels.HomeUiState
import com.wordsbyfarber.ui.viewmodels.HomeViewModel
import com.wordsbyfarber.ui.viewmodels.HomeMenuItem as HomeMenuItemType

@Composable
fun HomeScreen(
    onNavigateToLanguageSelection: () -> Unit,
    onNavigateToGame1: () -> Unit,
    onNavigateToGame2: () -> Unit,
    onNavigateToTopPlayers: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToFindWord: () -> Unit,
    onNavigateToTwoLetterWords: () -> Unit,
    onNavigateToThreeLetterWords: () -> Unit,
    onNavigateToRareLetter1Words: () -> Unit,
    onNavigateToRareLetter2Words: () -> Unit,
    onNavigateToPreferences: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToTermsOfService: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.shouldNavigateToLanguageSelection) {
        if (uiState.shouldNavigateToLanguageSelection) {
            viewModel.clearNavigationState()
            onNavigateToLanguageSelection()
        }
    }

    LaunchedEffect(uiState.shouldNavigateToMenuItem) {
        if (uiState.shouldNavigateToMenuItem) {
            when (uiState.selectedMenuItem) {
                HomeMenuItemType.Game1 -> onNavigateToGame1()
                HomeMenuItemType.Game2 -> onNavigateToGame2()
                HomeMenuItemType.TopPlayers -> onNavigateToTopPlayers()
                HomeMenuItemType.YourProfile -> onNavigateToProfile()
                HomeMenuItemType.FindWord -> onNavigateToFindWord()
                HomeMenuItemType.TwoLetterWords -> onNavigateToTwoLetterWords()
                HomeMenuItemType.ThreeLetterWords -> onNavigateToThreeLetterWords()
                HomeMenuItemType.RareLetter1Words -> onNavigateToRareLetter1Words()
                HomeMenuItemType.RareLetter2Words -> onNavigateToRareLetter2Words()
                HomeMenuItemType.Preferences -> onNavigateToPreferences()
                HomeMenuItemType.Help -> onNavigateToHelp()
                HomeMenuItemType.PrivacyPolicy -> onNavigateToPrivacyPolicy()
                HomeMenuItemType.TermsOfService -> onNavigateToTermsOfService()
                null -> { /* Do nothing */ }
            }
            viewModel.clearNavigationState()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    HomeContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onLanguageClick = {
            viewModel.switchLanguage()
        },
        onMenuItemClick = { menuItem ->
            viewModel.selectMenuItem(menuItem)
        }
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    snackbarHostState: SnackbarHostState,
    onLanguageClick: () -> Unit,
    onMenuItemClick: (HomeMenuItemType) -> Unit
) {
    Scaffold(
        topBar = {
            HomeTopBar(
                title = "Words by Farber",
                languageCode = uiState.currentLanguage?.code ?: "en",
                onLanguageClick = onLanguageClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.menuItems.isEmpty()) {
                LoadingIndicator(
                    message = "Loading menu...",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.menuItems,
                        key = { it.titleKey }
                    ) { menuItem ->
                        HomeMenuItem(
                            title = getMenuItemTitle(menuItem, uiState),
                            icon = getMenuItemIcon(menuItem),
                            onClick = { onMenuItemClick(menuItem) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getMenuItemTitle(
    menuItem: HomeMenuItemType,
    uiState: HomeUiState
): String {
    return when (menuItem) {
        HomeMenuItemType.Game1 -> "Game 1"
        HomeMenuItemType.Game2 -> "Game 2"
        HomeMenuItemType.TopPlayers -> "Top Players"
        HomeMenuItemType.YourProfile -> "Your Profile"
        HomeMenuItemType.FindWord -> "Find a Word"
        HomeMenuItemType.TwoLetterWords -> "2-letter Words"
        HomeMenuItemType.ThreeLetterWords -> "3-letter Words"
        HomeMenuItemType.RareLetter1Words -> "Words with ${uiState.currentLanguage?.rareLetter1 ?: "Q"}"
        HomeMenuItemType.RareLetter2Words -> "Words with ${uiState.currentLanguage?.rareLetter2 ?: "X"}"
        HomeMenuItemType.Preferences -> "Preferences"
        HomeMenuItemType.Help -> "Help"
        HomeMenuItemType.PrivacyPolicy -> "Privacy Policy"
        HomeMenuItemType.TermsOfService -> "Terms of Service"
    }
}

private fun getMenuItemIcon(menuItem: HomeMenuItemType): ImageVector {
    return when (menuItem) {
        HomeMenuItemType.Game1 -> Icons.Default.Games
        HomeMenuItemType.Game2 -> Icons.Default.Grid3x3
        HomeMenuItemType.TopPlayers -> Icons.Default.Leaderboard
        HomeMenuItemType.YourProfile -> Icons.Default.Person
        HomeMenuItemType.FindWord -> Icons.Default.Search
        HomeMenuItemType.TwoLetterWords -> Icons.Default.FilterList
        HomeMenuItemType.ThreeLetterWords -> Icons.Default.FilterList
        HomeMenuItemType.RareLetter1Words -> Icons.Default.Star
        HomeMenuItemType.RareLetter2Words -> Icons.Default.StarBorder
        HomeMenuItemType.Preferences -> Icons.Default.Settings
        HomeMenuItemType.Help -> Icons.Default.Help
        HomeMenuItemType.PrivacyPolicy -> Icons.Default.PrivacyTip
        HomeMenuItemType.TermsOfService -> Icons.Default.Description
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    WordsByFarberTheme {
        HomeContent(
            uiState = HomeUiState(
                currentLanguage = com.wordsbyfarber.data.models.Language(
                    code = "en",
                    name = "English (en)",
                    rareLetter1 = "Q",
                    rareLetter2 = "X",
                    hashedDictionaryUrl = "",
                    topUrl = "",
                    minWords = 270000,
                    myUid = 5
                ),
                wordCount = 270000,
                menuItems = listOf(
                    HomeMenuItemType.Game1,
                    HomeMenuItemType.Game2,
                    HomeMenuItemType.TopPlayers,
                    HomeMenuItemType.YourProfile,
                    HomeMenuItemType.FindWord
                )
            ),
            snackbarHostState = SnackbarHostState(),
            onLanguageClick = {},
            onMenuItemClick = {}
        )
    }
}