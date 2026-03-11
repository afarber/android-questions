package de.afarber.magicapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
    darkColorScheme(
        primary = MagicGreenDark,
        secondary = MagicGreenDark,
        tertiary = MagicGreenDark,
        background = DarkBackground,
        surface = DarkSurface,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = MagicGreenLight,
        secondary = MagicGreenLight,
        tertiary = MagicGreenLight,
        background = LightBackground,
        surface = LightSurface,
    )

@Composable
fun MagicAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
