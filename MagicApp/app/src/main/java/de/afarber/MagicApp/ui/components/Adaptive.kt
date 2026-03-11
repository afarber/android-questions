package de.afarber.MagicApp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun isWideScreen(minWidthDp: Int): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp >= minWidthDp
}
