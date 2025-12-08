package com.wordsbyfarber.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.wordsbyfarber.ui.screens.FailedDownloadScreen
import com.wordsbyfarber.ui.screens.HomeScreen
import com.wordsbyfarber.ui.screens.LanguageSelectionScreen
import com.wordsbyfarber.ui.screens.LoadingDictionaryScreen
import com.wordsbyfarber.viewmodel.DictionaryViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object LanguageSelectionRoute : NavKey

@Serializable
data class LoadingDictionaryRoute(val languageCode: String) : NavKey

@Serializable
data class HomeRoute(val languageCode: String) : NavKey

@Serializable
data class FailedDownloadRoute(val languageCode: String, val errorMessage: String) : NavKey

@Composable
fun AppNavigation(viewModel: DictionaryViewModel = koinViewModel()) {
    val backStack = rememberNavBackStack(LanguageSelectionRoute)

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<LanguageSelectionRoute> {
                LanguageSelectionScreen(
                    onLanguageSelected = { language ->
                        viewModel.selectLanguage(language)
                        backStack.add(LoadingDictionaryRoute(language.code))
                    }
                )
            }

            entry<LoadingDictionaryRoute> { route ->
                LoadingDictionaryScreen(
                    viewModel = viewModel,
                    onSuccess = {
                        backStack.clear()
                        backStack.add(LanguageSelectionRoute)
                        backStack.add(HomeRoute(route.languageCode))
                    },
                    onFailure = { error ->
                        backStack.removeLastOrNull()
                        backStack.add(FailedDownloadRoute(route.languageCode, error))
                    },
                    onBack = {
                        backStack.clear()
                        backStack.add(LanguageSelectionRoute)
                    }
                )
            }

            entry<HomeRoute> {
                HomeScreen(
                    viewModel = viewModel,
                    onBack = {
                        backStack.clear()
                        backStack.add(LanguageSelectionRoute)
                    }
                )
            }

            entry<FailedDownloadRoute> { route ->
                FailedDownloadScreen(
                    errorMessage = route.errorMessage,
                    onRetry = {
                        backStack.removeLastOrNull()
                        backStack.add(LoadingDictionaryRoute(route.languageCode))
                    },
                    onBack = {
                        backStack.clear()
                        backStack.add(LanguageSelectionRoute)
                    }
                )
            }
        }
    )
}
