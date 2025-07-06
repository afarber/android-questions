package com.wordsbyfarber.ui.navigation

// Sealed class defining all navigation routes for app screens
import com.wordsbyfarber.utils.Constants

sealed class Screen(val route: String) {
    object LanguageSelection : Screen(Constants.Screens.LANGUAGE_SELECTION)
    object LoadingDictionary : Screen(Constants.Screens.LOADING_DICTIONARY)
    object DownloadFailed : Screen(Constants.Screens.DOWNLOAD_FAILED)
    object Home : Screen(Constants.Screens.HOME)
    object Game1 : Screen(Constants.Screens.GAME_1)
    object Game2 : Screen(Constants.Screens.GAME_2)
    object TopPlayers : Screen(Constants.Screens.TOP_PLAYERS)
    object Profile : Screen(Constants.Screens.PROFILE)
    object FindWord : Screen(Constants.Screens.FIND_WORD)
    object TwoLetterWords : Screen(Constants.Screens.TWO_LETTER_WORDS)
    object ThreeLetterWords : Screen(Constants.Screens.THREE_LETTER_WORDS)
    object RareLetter1Words : Screen(Constants.Screens.RARE_LETTER_1_WORDS)
    object RareLetter2Words : Screen(Constants.Screens.RARE_LETTER_2_WORDS)
    object Preferences : Screen(Constants.Screens.PREFERENCES)
    object Help : Screen(Constants.Screens.HELP)
    object PrivacyPolicy : Screen(Constants.Screens.PRIVACY_POLICY)
    object TermsOfService : Screen(Constants.Screens.TERMS_OF_SERVICE)
}