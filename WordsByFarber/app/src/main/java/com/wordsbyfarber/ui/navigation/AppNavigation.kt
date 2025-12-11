/**
 * Navigation configuration using AndroidX Navigation 3.
 *
 * This file defines:
 * 1. Route classes - type-safe navigation destinations with parameters
 * 2. AppNavigation composable - the navigation host that displays screens
 *
 * Navigation 3 uses a declarative approach where routes are Kotlin classes
 * annotated with @Serializable, and navigation is done by adding/removing
 * routes from a back stack.
 */
package com.wordsbyfarber.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.wordsbyfarber.ui.screens.FailedDownloadScreen
import com.wordsbyfarber.ui.screens.HomeScreen
import com.wordsbyfarber.ui.screens.LanguageSelectionScreen
import com.wordsbyfarber.ui.screens.LoadingDictionaryScreen
import com.wordsbyfarber.util.LocaleManager
import com.wordsbyfarber.viewmodel.DictionaryViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

// ============================================================================
// Route Definitions
// ============================================================================
// Each route is a class that implements NavKey.
// @Serializable enables Navigation 3 to save/restore routes across config changes.
// data object = singleton route with no parameters
// data class = route with parameters (passed via constructor)

/**
 * Route for the language selection screen (start destination).
 *
 * data object = singleton, no parameters needed
 */
@Serializable
data object LanguageSelectionRoute : NavKey

/**
 * Route for the dictionary download/loading screen.
 *
 * @property languageCode The language being downloaded (e.g., "de", "en")
 */
@Serializable
data class LoadingDictionaryRoute(val languageCode: String) : NavKey

/**
 * Route for the main home screen after dictionary is loaded.
 *
 * @property languageCode The currently loaded language
 */
@Serializable
data class HomeRoute(val languageCode: String) : NavKey

/**
 * Route for the error screen when download fails.
 *
 * @property languageCode The language that failed to download
 * @property errorMessage The error message to display
 */
@Serializable
data class FailedDownloadRoute(val languageCode: String, val errorMessage: String) : NavKey

// ============================================================================
// Navigation Host
// ============================================================================

/**
 * Main navigation composable that manages screen transitions.
 *
 * @param viewModel Shared ViewModel instance (injected by Koin).
 *                  Default parameter uses koinViewModel() to get it from Koin.
 *
 * koinViewModel() = Koin extension that provides ViewModel scoped to the composition
 */
@Composable
fun AppNavigation(viewModel: DictionaryViewModel = koinViewModel()) {
    // rememberNavBackStack creates a navigation stack that survives recomposition
    // LanguageSelectionRoute is the initial/start destination
    val backStack = rememberNavBackStack(LanguageSelectionRoute)

    // NavDisplay renders the current screen based on the back stack
    // entryProvider defines which composable to show for each route type
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {

            // entry<RouteType> { } defines the composable for that route
            // The lambda receives the route instance with its parameters
            entry<LanguageSelectionRoute> {
                LanguageSelectionScreen(
                    onLanguageSelected = { language ->
                        // Switch app UI language to match selected dictionary
                        LocaleManager.setLanguage(language.code)
                        viewModel.setLanguage(language)
                        // Navigate forward by adding to the back stack
                        backStack.add(LoadingDictionaryRoute(language.code))
                    }
                )
            }

            // { route -> } gives access to route parameters (languageCode)
            entry<LoadingDictionaryRoute> { route ->
                LoadingDictionaryScreen(
                    viewModel = viewModel,
                    onSuccess = {
                        // Reset stack to: [LanguageSelection, Home]
                        // clear() removes all entries, then we rebuild
                        backStack.clear()
                        backStack.add(LanguageSelectionRoute)
                        backStack.add(HomeRoute(route.languageCode))
                    },
                    onFailure = { error ->
                        // Replace loading screen with error screen
                        backStack.removeLastOrNull()
                        backStack.add(FailedDownloadRoute(route.languageCode, error))
                    },
                    onBack = {
                        // Go back to language selection
                        backStack.clear()
                        backStack.add(LanguageSelectionRoute)
                    }
                )
            }

            entry<HomeRoute> {
                HomeScreen(
                    viewModel = viewModel,
                    onBack = {
                        // Return to language selection (start fresh)
                        backStack.clear()
                        backStack.add(LanguageSelectionRoute)
                    }
                )
            }

            entry<FailedDownloadRoute> { route ->
                FailedDownloadScreen(
                    errorMessage = route.errorMessage,
                    onRetry = {
                        // Replace error screen with loading screen to retry
                        backStack.removeLastOrNull()
                        backStack.add(LoadingDictionaryRoute(route.languageCode))
                    },
                    onBack = {
                        // Return to language selection
                        backStack.clear()
                        backStack.add(LanguageSelectionRoute)
                    }
                )
            }
        }
    )
}
