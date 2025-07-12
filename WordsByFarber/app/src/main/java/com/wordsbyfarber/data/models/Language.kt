package com.wordsbyfarber.data.models

// Data class representing a supported language with its configuration
data class Language(
    val code: String,
    val name: String,
    val rareLetter1: String,
    val rareLetter2: String,
    val hashedDictionaryUrl: String,
    val topUrl: String,
    val minWords: Int,
    // myUid is currently used to select a user to be displayed in "My profile" screen,
    // it will be removed after Google/Huawei/Amazon logins are implemented
    val myUid: Int
) {
    companion object {
        // Static language configurations based on CLAUDE.md specifications
        val SUPPORTED_LANGUAGES = mapOf(
            "de" to Language(
                code = "de",
                name = "Deutsch",
                rareLetter1 = "Q",
                rareLetter2 = "Y",
                hashedDictionaryUrl = "https://wordsbyfarber.com/Consts-de.js",
                topUrl = "https://wordsbyfarber.com/de/top-all",
                minWords = 180_000,
                myUid = 5
            ),
            "en" to Language(
                code = "en",
                name = "English",
                rareLetter1 = "Q",
                rareLetter2 = "X",
                hashedDictionaryUrl = "https://wordsbyfarber.com/Consts-en.js",
                topUrl = "https://wordsbyfarber.com/en/top-all",
                minWords = 270_000,
                myUid = 5
            ),
            "fr" to Language(
                code = "fr",
                name = "Français",
                rareLetter1 = "K",
                rareLetter2 = "W",
                hashedDictionaryUrl = "https://wordsbyfarber.com/Consts-fr.js",
                topUrl = "https://wordsbyfarber.com/fr/top-all",
                minWords = 370_000,
                myUid = 5
            ),
            "nl" to Language(
                code = "nl",
                name = "Nederlands",
                rareLetter1 = "Q",
                rareLetter2 = "X",
                hashedDictionaryUrl = "https://wordsbyfarber.com/Consts-nl.js",
                topUrl = "https://wordsbyfarber.com/nl/top-all",
                minWords = 130_000,
                myUid = 5
            ),
            "pl" to Language(
                code = "pl",
                name = "Polski",
                rareLetter1 = "Ń",
                rareLetter2 = "Ź",
                hashedDictionaryUrl = "https://wordsbyfarber.com/Consts-pl.js",
                topUrl = "https://wordsbyfarber.com/pl/top-all",
                minWords = 3_000_000,
                myUid = 5
            ),
            "ru" to Language(
                code = "ru",
                name = "Русский",
                rareLetter1 = "Ъ",
                rareLetter2 = "Э",
                hashedDictionaryUrl = "https://wordsbyfarber.com/Consts-ru.js",
                topUrl = "https://wordsbyfarber.com/ru/top-all",
                minWords = 120_000,
                myUid = 5
            )
        )
        
        val SUPPORTED_LANGUAGE_CODES = SUPPORTED_LANGUAGES.keys.toList()
        
        fun getLanguage(code: String): Language? = SUPPORTED_LANGUAGES[code]
        
        fun getAllLanguages(): List<Language> = SUPPORTED_LANGUAGES.values.toList()
    }
}