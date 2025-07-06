package com.wordsbyfarber.ui.navigation

// Navigation component managing screen transitions and navigation logic
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wordsbyfarber.ui.screens.LanguageSelectionScreen
import com.wordsbyfarber.ui.screens.LoadingDictionaryScreen
import com.wordsbyfarber.ui.screens.DownloadFailedScreen
import com.wordsbyfarber.ui.screens.HomeScreen
import com.wordsbyfarber.ui.screens.Game1Screen
import com.wordsbyfarber.ui.screens.Game2Screen
import com.wordsbyfarber.ui.viewmodels.LanguageSelectionViewModel
import com.wordsbyfarber.ui.viewmodels.LoadingDictionaryViewModel
import com.wordsbyfarber.ui.viewmodels.DownloadFailedViewModel
import com.wordsbyfarber.ui.viewmodels.HomeViewModel
import com.wordsbyfarber.ui.viewmodels.GameViewModel
import com.wordsbyfarber.data.repository.PreferencesRepository
import com.wordsbyfarber.data.repository.DictionaryRepository

@Composable
fun AppNavigation(
    navController: NavHostController,
    preferencesRepository: PreferencesRepository,
    dictionaryRepository: DictionaryRepository
) {
    val context = LocalContext.current
    
    // Determine start destination based on app state
    val startDestination = Screen.LanguageSelection.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.LanguageSelection.route) {
            val viewModel: LanguageSelectionViewModel = viewModel(
                factory = LanguageSelectionViewModel.Factory(preferencesRepository, dictionaryRepository)
            )
            LanguageSelectionScreen(
                viewModel = viewModel,
                onLanguageSelected = { language ->
                    navController.navigate(Screen.LoadingDictionary.route) {
                        popUpTo(Screen.LanguageSelection.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.LoadingDictionary.route) {
            val viewModel: LoadingDictionaryViewModel = viewModel(
                factory = LoadingDictionaryViewModel.Factory(preferencesRepository, dictionaryRepository, context)
            )
            val uiState by viewModel.uiState.collectAsState()
            
            LoadingDictionaryScreen(
                uiState = uiState,
                onCancel = {
                    viewModel.cancelDownload()
                    navController.navigate(Screen.LanguageSelection.route) {
                        popUpTo(Screen.LoadingDictionary.route) { inclusive = true }
                    }
                },
                onDownloadComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.LoadingDictionary.route) { inclusive = true }
                    }
                },
                onDownloadFailed = {
                    navController.navigate(Screen.DownloadFailed.route) {
                        popUpTo(Screen.LoadingDictionary.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.DownloadFailed.route) {
            val viewModel: DownloadFailedViewModel = viewModel(
                factory = DownloadFailedViewModel.Factory(preferencesRepository)
            )
            DownloadFailedScreen(
                viewModel = viewModel,
                onRetry = {
                    navController.navigate(Screen.LoadingDictionary.route) {
                        popUpTo(Screen.DownloadFailed.route) { inclusive = true }
                    }
                },
                onCancel = {
                    navController.navigate(Screen.LanguageSelection.route) {
                        popUpTo(Screen.DownloadFailed.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(preferencesRepository, dictionaryRepository)
            )
            HomeScreen(
                viewModel = viewModel,
                onNavigateToScreen = { screen ->
                    navController.navigate(screen)
                },
                onLanguageButtonClick = {
                    navController.navigate(Screen.LanguageSelection.route)
                }
            )
        }

        composable(Screen.Game1.route) {
            val viewModel: GameViewModel = viewModel()
            Game1Screen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Game2.route) {
            val viewModel: GameViewModel = viewModel()
            Game2Screen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // TODO: Add remaining screens (7-17) as needed
    }
}