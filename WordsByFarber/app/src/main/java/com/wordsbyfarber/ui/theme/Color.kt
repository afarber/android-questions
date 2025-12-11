/**
 * Color definitions for the app theme.
 *
 * Material 3 uses a tonal color system. The number suffix indicates luminance:
 * - 80 = lighter variants (used for dark theme, because light colors on dark background)
 * - 40 = darker variants (used for light theme, because dark colors on light background)
 *
 * Color(0xFFD0BCFF) = ARGB hex color
 * - 0x = hexadecimal prefix
 * - FF = alpha (fully opaque)
 * - D0BCFF = RGB values
 */
package com.wordsbyfarber.ui.theme

import androidx.compose.ui.graphics.Color

// Dark theme colors (lighter, to contrast with dark backgrounds)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

// Light theme colors (darker, to contrast with light backgrounds)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)