package com.wordsbyfarber.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wordsbyfarber.ui.screens.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = Screen.LanguageSelection.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.LanguageSelection.route) {
            LanguageSelectionScreen(
                viewModel = hiltViewModel(),
                onLanguageSelected = { languageCode: String ->
                    // Navigation logic will be handled in ViewModel
                },
                navController = navController
            )
        }
        
        composable(Screen.LoadingDictionary.route) {
            LoadingDictionaryScreen(
                viewModel = hiltViewModel(),
                onCloseClicked = {
                    navController.navigate(Screen.LanguageSelection.route) {
                        popUpTo(Screen.LanguageSelection.route) { inclusive = true }
                    }
                },
                onDownloadComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.LanguageSelection.route) { inclusive = true }
                    }
                },
                onDownloadFailed = {
                    navController.navigate(Screen.DownloadFailed.route)
                }
            )
        }
        
        composable(Screen.DownloadFailed.route) {
            DownloadFailedScreen(
                onCloseClicked = {
                    navController.navigate(Screen.LanguageSelection.route) {
                        popUpTo(Screen.LanguageSelection.route) { inclusive = true }
                    }
                },
                onRetryClicked = {
                    navController.navigate(Screen.LoadingDictionary.route)
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = hiltViewModel(),
                onMenuItemClicked = { menuItem: String ->
                    when (menuItem) {
                        "Game 1" -> navController.navigate(Screen.Game1.route)
                        "Game 2" -> navController.navigate(Screen.Game2.route)
                        "Top players" -> navController.navigate(Screen.TopPlayers.route)
                        "Your profile" -> navController.navigate(Screen.Profile.route)
                        "Find a word" -> navController.navigate(Screen.FindWord.route)
                        "2-letter words" -> navController.navigate(Screen.TwoLetterWords.route)
                        "3-letter words" -> navController.navigate(Screen.ThreeLetterWords.route)
                        "Words with rare letter 1" -> navController.navigate(Screen.RareLetter1Words.route)
                        "Words with rare letter 2" -> navController.navigate(Screen.RareLetter2Words.route)
                        "Preferences" -> navController.navigate(Screen.Preferences.route)
                        "Help" -> navController.navigate(Screen.Help.route)
                        "Privacy policy" -> navController.navigate(Screen.PrivacyPolicy.route)
                        "Terms of service" -> navController.navigate(Screen.TermsOfService.route)
                    }
                },
                onLanguageButtonClicked = {
                    navController.navigate(Screen.LanguageSelection.route)
                }
            )
        }
        
        composable(Screen.Game1.route) {
            Game1Screen(
                onCloseClicked = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Game2.route) {
            Game2Screen(
                onCloseClicked = { navController.popBackStack() }
            )
        }
        
        composable(Screen.TopPlayers.route) {
            TopPlayersScreen(
                viewModel = hiltViewModel(),
                onCloseClicked = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = hiltViewModel(),
                onCloseClicked = { navController.popBackStack() }
            )
        }
        
        composable(Screen.FindWord.route) {
            FindWordScreen(
                viewModel = hiltViewModel(),
                onCloseClicked = { navController.popBackStack() }
            )
        }
        
        composable(Screen.TwoLetterWords.route) {
            TwoLetterWordsScreen(
                viewModel = hiltViewModel(),
                onCloseClicked = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ThreeLetterWords.route) {
            ThreeLetterWordsScreen(
                viewModel = hiltViewModel(),
                onCloseClicked = { navController.popBackStack() }
            )
        }
        
        composable(Screen.RareLetter1Words.route) {
            RareLetter1Screen(
                viewModel = hiltViewModel(),
                onCloseClicked = { navController.popBackStack() }
            )
        }
        
        composable(Screen.RareLetter2Words.route) {
            RareLetter2Screen(
                viewModel = hiltViewModel(),
                onCloseClicked = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Preferences.route) {
            PreferencesScreen(
                onCloseClicked = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Help.route) {
            HelpScreen(
                onCloseClicked = { navController.popBackStack() }
            )
        }
        
        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(
                onCloseClicked = { navController.popBackStack() }
            )
        }
        
        composable(Screen.TermsOfService.route) {
            TermsOfServiceScreen(
                onCloseClicked = { navController.popBackStack() }
            )
        }
    }
}