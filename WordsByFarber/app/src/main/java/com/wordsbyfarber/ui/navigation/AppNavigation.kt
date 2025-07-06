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
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.LanguageSelection.route) { inclusive = true }
                    }
                },
                onNavigateToLoading = {
                    navController.navigate(Screen.LoadingDictionary.route)
                }
            )
        }
        
        composable(Screen.LoadingDictionary.route) {
            LoadingDictionaryScreen(
                viewModel = hiltViewModel(),
                languageCode = "en", // TODO: Pass actual language code
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.LanguageSelection.route) { inclusive = true }
                    }
                },
                onNavigateToError = {
                    navController.navigate(Screen.DownloadFailed.route)
                },
                onNavigateToLanguageSelection = {
                    navController.navigate(Screen.LanguageSelection.route) {
                        popUpTo(Screen.LanguageSelection.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.DownloadFailed.route) {
            DownloadFailedScreen(
                viewModel = hiltViewModel(),
                onNavigateToLoading = {
                    navController.navigate(Screen.LoadingDictionary.route)
                },
                onNavigateToLanguageSelection = {
                    navController.navigate(Screen.LanguageSelection.route) {
                        popUpTo(Screen.LanguageSelection.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = hiltViewModel(),
                onNavigateToLanguageSelection = {
                    navController.navigate(Screen.LanguageSelection.route)
                },
                onNavigateToGame1 = {
                    navController.navigate(Screen.Game1.route)
                },
                onNavigateToGame2 = {
                    navController.navigate(Screen.Game2.route)
                },
                onNavigateToTopPlayers = {
                    navController.navigate(Screen.TopPlayers.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToFindWord = {
                    navController.navigate(Screen.FindWord.route)
                },
                onNavigateToTwoLetterWords = {
                    navController.navigate(Screen.TwoLetterWords.route)
                },
                onNavigateToThreeLetterWords = {
                    navController.navigate(Screen.ThreeLetterWords.route)
                },
                onNavigateToRareLetter1Words = {
                    navController.navigate(Screen.RareLetter1Words.route)
                },
                onNavigateToRareLetter2Words = {
                    navController.navigate(Screen.RareLetter2Words.route)
                },
                onNavigateToPreferences = {
                    navController.navigate(Screen.Preferences.route)
                },
                onNavigateToHelp = {
                    navController.navigate(Screen.Help.route)
                },
                onNavigateToPrivacyPolicy = {
                    navController.navigate(Screen.PrivacyPolicy.route)
                },
                onNavigateToTermsOfService = {
                    navController.navigate(Screen.TermsOfService.route)
                }
            )
        }
        
        composable(Screen.Game1.route) {
            Game1Screen(
                viewModel = hiltViewModel(),
                onNavigateToHome = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Game2.route) {
            Game2Screen(
                viewModel = hiltViewModel(),
                onNavigateToHome = { navController.popBackStack() }
            )
        }
        
        composable(Screen.TopPlayers.route) {
            TopPlayersScreen(
                viewModel = hiltViewModel(),
                onNavigateToHome = { navController.popBackStack() },
                onNavigateToLanguageSelection = {
                    navController.navigate(Screen.LanguageSelection.route)
                }
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = hiltViewModel(),
                onNavigateToHome = { navController.popBackStack() },
                onNavigateToLanguageSelection = {
                    navController.navigate(Screen.LanguageSelection.route)
                }
            )
        }
        
        composable(Screen.FindWord.route) {
            FindWordScreen(
                viewModel = hiltViewModel(),
                onNavigateToHome = { navController.popBackStack() },
                onNavigateToLanguageSelection = {
                    navController.navigate(Screen.LanguageSelection.route)
                }
            )
        }
        
        composable(Screen.TwoLetterWords.route) {
            TwoLetterWordsScreen(
                viewModel = hiltViewModel(),
                onNavigateToHome = { navController.popBackStack() },
                onNavigateToLanguageSelection = {
                    navController.navigate(Screen.LanguageSelection.route)
                }
            )
        }
        
        composable(Screen.ThreeLetterWords.route) {
            ThreeLetterWordsScreen(
                viewModel = hiltViewModel(),
                onNavigateToHome = { navController.popBackStack() },
                onNavigateToLanguageSelection = {
                    navController.navigate(Screen.LanguageSelection.route)
                }
            )
        }
        
        composable(Screen.RareLetter1Words.route) {
            RareLetter1Screen(
                viewModel = hiltViewModel(),
                onNavigateToHome = { navController.popBackStack() },
                onNavigateToLanguageSelection = {
                    navController.navigate(Screen.LanguageSelection.route)
                }
            )
        }
        
        composable(Screen.RareLetter2Words.route) {
            RareLetter2Screen(
                viewModel = hiltViewModel(),
                onNavigateToHome = { navController.popBackStack() },
                onNavigateToLanguageSelection = {
                    navController.navigate(Screen.LanguageSelection.route)
                }
            )
        }
        
        composable(Screen.Preferences.route) {
            PreferencesScreen(
                onNavigateToHome = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Help.route) {
            HelpScreen(
                viewModel = hiltViewModel(),
                onNavigateToHome = { navController.popBackStack() }
            )
        }
        
        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(
                viewModel = hiltViewModel(),
                onNavigateToHome = { navController.popBackStack() }
            )
        }
        
        composable(Screen.TermsOfService.route) {
            TermsOfServiceScreen(
                viewModel = hiltViewModel(),
                onNavigateToHome = { navController.popBackStack() }
            )
        }
    }
}