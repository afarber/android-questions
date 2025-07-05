package com.wordsbyfarber.utils

import java.security.MessageDigest

object StringUtils {
    
    fun hashWord(word: String, rareLetter1: String = "Ń", rareLetter2: String = "Ź"): String {
        return when {
            word.length <= 3 -> word
            word.contains(rareLetter1, ignoreCase = true) -> word
            word.contains(rareLetter2, ignoreCase = true) -> word
            else -> {
                val saltedWord = Constants.HASH_SALT + word
                val md5Hash = md5(saltedWord)
                md5Hash.take(Constants.MD5_SUBSTRING_LENGTH)
            }
        }
    }
    
    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    fun formatTime(timeString: String): String {
        return timeString.takeIf { it.isNotBlank() } ?: "00:00"
    }
    
    fun formatScore(score: Double): String {
        return "%.1f".format(score)
    }
    
    fun isValidLanguageCode(code: String): Boolean {
        return Constants.SUPPORTED_LANGUAGES.contains(code.lowercase())
    }
}