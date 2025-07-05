package com.wordsbyfarber.domain.models

data class WordSearchResult(
    val word: String,
    val explanation: String,
    val isFound: Boolean
)