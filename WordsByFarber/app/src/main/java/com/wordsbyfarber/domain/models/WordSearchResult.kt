package com.wordsbyfarber.domain.models

// Domain model representing the result of a word search operation
data class WordSearchResult(
    val word: String,
    val explanation: String,
    val isFound: Boolean
)