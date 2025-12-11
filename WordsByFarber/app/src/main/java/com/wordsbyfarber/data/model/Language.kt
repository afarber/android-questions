/**
 * Language model and supported languages configuration.
 *
 * This file defines the Language data class and the list of all supported
 * dictionary languages with their configuration (URLs, expected word counts, etc.).
 */
package com.wordsbyfarber.data.model

/**
 * Represents a supported dictionary language with all its configuration.
 *
 * data class = Kotlin auto-generates equals(), hashCode(), toString(), copy()
 * All properties are val = immutable (cannot be changed after creation)
 *
 * @property code ISO 639-1 language code (e.g., "de", "en", "fr")
 * @property name Display name in the native language (e.g., "Deutsch", "English")
 * @property rareLetter1 First rare letter for this language (for Scrabble strategy)
 * @property rareLetter2 Second rare letter for this language
 * @property hashedDictionaryUrl URL to download the dictionary JavaScript file
 * @property topUrl URL for the top players leaderboard
 * @property minWords Expected minimum word count (used for progress calculation)
 * @property myUid User ID for the leaderboard
 */
data class Language(
    val code: String,
    val name: String,
    val rareLetter1: String,
    val rareLetter2: String,
    val hashedDictionaryUrl: String,
    val topUrl: String,
    val minWords: Int,
    val myUid: Int
)

/**
 * Singleton object containing all supported languages.
 *
 * object = Kotlin singleton, only one instance exists
 * Accessed as SupportedLanguages.languages
 */
object SupportedLanguages {
    /**
     * Map of language code to Language configuration.
     *
     * mapOf() creates an immutable Map<String, Language>
     * Keys are ISO 639-1 codes, values are Language instances
     */
    val languages = mapOf(
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
}
