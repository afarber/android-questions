package com.wordsbyfarber.ui.navigation

// Sealed class defining all navigation routes for app screens
import com.wordsbyfarber.utils.Constants

sealed class Screen(val route: String) {
    data object LanguageSelection : Screen(Constants.Screens.LANGUAGE_SELECTION)
    data object LoadingDictionary : Screen(Constants.Screens.LOADING_DICTIONARY)
    data object DownloadFailed : Screen(Constants.Screens.DOWNLOAD_FAILED)
    data object Home : Screen(Constants.Screens.HOME)
    data object Game1 : Screen(Constants.Screens.GAME_1)
    data object Game2 : Screen(Constants.Screens.GAME_2)
    data object TopPlayers : Screen(Constants.Screens.TOP_PLAYERS)
    data object Profile : Screen(Constants.Screens.PROFILE)
    data object FindWord : Screen(Constants.Screens.FIND_WORD)
    data object TwoLetterWords : Screen(Constants.Screens.TWO_LETTER_WORDS)
    data object ThreeLetterWords : Screen(Constants.Screens.THREE_LETTER_WORDS)
    data object RareLetter1Words : Screen(Constants.Screens.RARE_LETTER_1_WORDS)
    data object RareLetter2Words : Screen(Constants.Screens.RARE_LETTER_2_WORDS)
    data object Preferences : Screen(Constants.Screens.PREFERENCES)
    data object Help : Screen(Constants.Screens.HELP)
    data object PrivacyPolicy : Screen(Constants.Screens.PRIVACY_POLICY)
    data object TermsOfService : Screen(Constants.Screens.TERMS_OF_SERVICE)
}