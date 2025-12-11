/**
 * Typography (text style) definitions for the app theme.
 *
 * Material 3 defines a type scale with different text styles for different purposes:
 * - display: Large, impactful text (headlines on landing pages)
 * - headline: Section headers
 * - title: Smaller headers (cards, dialogs)
 * - body: Main content text
 * - label: Small text (buttons, tabs, captions)
 *
 * Each category has Large, Medium, and Small variants.
 */
package com.wordsbyfarber.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * App typography configuration.
 *
 * Typography() creates a full type scale. We only customize bodyLarge here;
 * other styles use Material 3 defaults.
 *
 * .sp = scaleable pixels, respects user's font size accessibility settings
 */
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        // 16.sp = 16 scaleable pixels (standard body text size)
        fontSize = 16.sp,
        // lineHeight = vertical space between lines of text
        lineHeight = 24.sp,
        // letterSpacing = horizontal space between characters
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)