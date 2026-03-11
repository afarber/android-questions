package de.afarber.magicapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo

@Composable
fun isWideScreen(minWidthDp: Int): Boolean {
    val density = LocalDensity.current
    val widthDp =
        with(density) {
            LocalWindowInfo.current.containerSize.width
                .toDp()
                .value
        }
    return widthDp >= minWidthDp
}
