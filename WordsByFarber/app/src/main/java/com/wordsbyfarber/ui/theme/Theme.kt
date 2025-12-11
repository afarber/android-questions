/**
 * Material 3 theme configuration for the app.
 *
 * This file defines color schemes and the main theme composable that wraps
 * the entire app UI. It supports:
 * - Light/dark theme (follows system setting)
 * - Dynamic colors on Android 12+ (colors extracted from wallpaper)
 * - Fallback to static colors on older Android versions
 */
package com.wordsbyfarber.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Static color scheme for dark theme (used on Android < 12 or if dynamic color disabled)
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

// Static color scheme for light theme
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

/**
 * Main theme composable that provides Material 3 theming to the app.
 *
 * Wrap your root composable with this to apply consistent styling.
 *
 * @param darkTheme Whether to use dark theme. Defaults to system setting.
 *                  isSystemInDarkTheme() reads from Android system preferences.
 * @param dynamicColor Whether to use Android 12+ dynamic colors from wallpaper.
 * @param content The composable content to be themed.
 *                () -> Unit = a lambda that takes no parameters and returns nothing.
 *                @Composable = this lambda contains composable code.
 */
@Composable
fun WordsByFarberTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // when {} = Kotlin's enhanced switch statement, can use conditions
    val colorScheme = when {
        // Android 12+ (API 31, codename S) supports dynamic colors
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            // LocalContext.current = Compose way to get Android Context
            val context = LocalContext.current
            // Dynamic color schemes are generated from the user's wallpaper
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Fallback to static color schemes for older Android
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // MaterialTheme provides colors, typography, and shapes to all child composables
    // Child composables access these via MaterialTheme.colorScheme, MaterialTheme.typography, etc.
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}