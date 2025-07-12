package com.wordsbyfarber.utils

// Application constants including supported languages, screen routes, and preferences keys
object Constants {
    const val HASH_SALT = "ECHO"
    const val MD5_SUBSTRING_LENGTH = 16
    
    val SUPPORTED_LANGUAGES = listOf("de", "en", "fr", "nl", "pl", "ru")
    
    object Screens {
        const val LANGUAGE_SELECTION = "language_selection"
        const val LOADING_DICTIONARY = "loading_dictionary"
        const val DOWNLOAD_FAILED = "download_failed"
        const val HOME = "home"
        const val GAME_1 = "game_1"
        const val GAME_2 = "game_2"
        const val TOP_PLAYERS = "top_players"
        const val PROFILE = "profile"
        const val FIND_WORD = "find_word"
        const val TWO_LETTER_WORDS = "two_letter_words"
        const val THREE_LETTER_WORDS = "three_letter_words"
        const val RARE_LETTER_1_WORDS = "rare_letter_1_words"
        const val RARE_LETTER_2_WORDS = "rare_letter_2_words"
        const val PREFERENCES = "preferences"
        const val HELP = "help"
        const val PRIVACY_POLICY = "privacy_policy"
        const val TERMS_OF_SERVICE = "terms_of_service"
    }
    
    object Preferences {
        const val PREFS_NAME = "words_by_farber_prefs"
        const val KEY_LANGUAGE = "language"
    }
}