package com.wordsbyfarber.domain.models

data class LanguageInfo(
    val code: String,
    val displayName: String,
    val flagIcon: String, // SVG path or resource identifier
    val isSelected: Boolean = false
)