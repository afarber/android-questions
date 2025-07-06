package com.wordsbyfarber.domain.models

// Domain model representing language information for UI display
data class LanguageInfo(
    val code: String,
    val displayName: String,
    val flagIcon: String, // SVG path or resource identifier
    val isSelected: Boolean = false
)