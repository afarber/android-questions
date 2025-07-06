package com.wordsbyfarber.data.models

// Data class representing a word item for display in UI lists
data class WordItem(
    val word: String,
    val explanation: String = ""
)