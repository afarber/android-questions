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
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.ViewModule
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wordsbyfarber.R
import com.wordsbyfarber.data.models.Language
import com.wordsbyfarber.ui.components.HomeMenuItem
import com.wordsbyfarber.ui.components.HomeTopBar
import com.wordsbyfarber.ui.components.LoadingIndicator
import com.wordsbyfarber.ui.navigation.Screen
import com.wordsbyfarber.ui.theme.WordsByFarberTheme
import com.wordsbyfarber.ui.viewmodels.HomeViewModel
import com.wordsbyfarber.ui.viewmodels.HomeUiState

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToScreen: (String) -> Unit,
    onLanguageButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle navigation
    LaunchedEffect(uiState.shouldNavigateToLanguageSelection) {
        if (uiState.shouldNavigateToLanguageSelection) {
            onLanguageButtonClick()
            viewModel.clearNavigationState()
        }
    }

    LaunchedEffect(uiState.shouldNavigateToMenuItem, uiState.selectedMenuItem) {
        if (uiState.shouldNavigateToMenuItem && uiState.selectedMenuItem != null) {
            val route = when (uiState.selectedMenuItem) {
                com.wordsbyfarber.ui.viewmodels.HomeMenuItem.Game1 -> Screen.Game1.route
                com.wordsbyfarber.ui.viewmodels.HomeMenuItem.Game2 -> Screen.Game2.route
                else -> Screen.Game1.route // For now, default to Game1 for unimplemented screens
            }
            onNavigateToScreen(route)
            viewModel.clearNavigationState()
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
        topBar = {
            HomeTopBar(
                title = stringResource(R.string.app_name),
                languageCode = uiState.currentLanguage?.code ?: "en",
                onLanguageClick = { viewModel.switchLanguage() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        HomeContent(
            uiState = uiState,
            onMenuItemClick = { item -> viewModel.selectMenuItem(item) },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onMenuItemClick: (com.wordsbyfarber.ui.viewmodels.HomeMenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            uiState.currentLanguage == null -> {
                LoadingIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            
            uiState.menuItems.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.menuItems) { menuItem ->
                        HomeMenuItem(
                            title = getMenuItemTitle(menuItem),
                            icon = getMenuItemIcon(menuItem),
                            onClick = { onMenuItemClick(menuItem) }
                        )
                    }
                }
            }
            
            else -> {
                Text(
                    text = stringResource(R.string.no_menu_items),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun getMenuItemTitle(menuItem: com.wordsbyfarber.ui.viewmodels.HomeMenuItem): String {
    return when (menuItem) {
        com.wordsbyfarber.ui.viewmodels.HomeMenuItem.Game1 -> stringResource(R.string.game_1)
        com.wordsbyfarber.ui.viewmodels.HomeMenuItem.Game2 -> stringResource(R.string.game_2)
        com.wordsbyfarber.ui.viewmodels.HomeMenuItem.TopPlayers -> stringResource(R.string.top_players)
        com.wordsbyfarber.ui.viewmodels.HomeMenuItem.YourProfile -> stringResource(R.string.your_profile)
        com.wordsbyfarber.ui.viewmodels.HomeMenuItem.FindWord -> stringResource(R.string.find_word)
        com.wordsbyfarber.ui.viewmodels.HomeMenuItem.TwoLetterWords -> stringResource(R.string.two_letter_words)
        com.wordsbyfarber.ui.viewmodels.HomeMenuItem.ThreeLetterWords -> stringResource(R.string.three_letter_words)
        com.wordsbyfarber.ui.viewmodels.HomeMenuItem.RareLetter1Words -> stringResource(R.string.rare_letter_1_words)
        com.wordsbyfarber.ui.viewmodels.HomeMenuItem.RareLetter2Words -> stringResource(R.string.rare_letter_2_words)
        com.wordsbyfarber.ui.viewmodels.HomeMenuItem.Preferences -> stringResource(R.string.preferences)
        com.wordsbyfarber.ui.viewmodels.HomeMenuItem.Help -> stringResource(R.string.help)
        com.wordsbyfarber.ui.viewmodels.HomeMenuItem.PrivacyPolicy -> stringResource(R.string.privacy_policy)
        com.wordsbyfarber.ui.viewmodels.HomeMenuItem.TermsOfService -> stringResource(R.string.terms_of_service)
    }
}

@Composable
private fun getMenuItemIcon(menuItem: com.wordsbyfarber.ui.viewmodels.HomeMenuItem) = when (menuItem) {
    com.wordsbyfarber.ui.viewmodels.HomeMenuItem.Game1 -> Icons.Default.GridOn
    com.wordsbyfarber.ui.viewmodels.HomeMenuItem.Game2 -> Icons.Default.ViewModule
    com.wordsbyfarber.ui.viewmodels.HomeMenuItem.TopPlayers -> Icons.Default.Leaderboard
    com.wordsbyfarber.ui.viewmodels.HomeMenuItem.YourProfile -> Icons.Default.Person
    com.wordsbyfarber.ui.viewmodels.HomeMenuItem.FindWord -> Icons.Default.Search
    com.wordsbyfarber.ui.viewmodels.HomeMenuItem.TwoLetterWords -> Icons.Default.FilterAlt
    com.wordsbyfarber.ui.viewmodels.HomeMenuItem.ThreeLetterWords -> Icons.Default.FilterAlt
    com.wordsbyfarber.ui.viewmodels.HomeMenuItem.RareLetter1Words -> Icons.Default.Star
    com.wordsbyfarber.ui.viewmodels.HomeMenuItem.RareLetter2Words -> Icons.Default.StarBorder
    com.wordsbyfarber.ui.viewmodels.HomeMenuItem.Preferences -> Icons.Default.Settings
    com.wordsbyfarber.ui.viewmodels.HomeMenuItem.Help -> Icons.Default.Help
    com.wordsbyfarber.ui.viewmodels.HomeMenuItem.PrivacyPolicy -> Icons.Default.PrivacyTip
    com.wordsbyfarber.ui.viewmodels.HomeMenuItem.TermsOfService -> Icons.Default.Description
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    WordsByFarberTheme {
        HomeContent(
            uiState = HomeUiState(
                currentLanguage = Language(
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
                    com.wordsbyfarber.ui.viewmodels.HomeMenuItem.Game1,
                    com.wordsbyfarber.ui.viewmodels.HomeMenuItem.Game2,
                    com.wordsbyfarber.ui.viewmodels.HomeMenuItem.TopPlayers
                )
            ),
            onMenuItemClick = { }
        )
    }
}