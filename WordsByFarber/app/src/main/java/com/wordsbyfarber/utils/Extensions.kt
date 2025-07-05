package com.wordsbyfarber.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler by rememberUpdatedState(onEvent)
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler(owner, event)
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

fun String.toLanguageDisplayName(): String {
    return when (this.lowercase()) {
        "de" -> "Deutsch (de)"
        "en" -> "English (en)"
        "fr" -> "Français (fr)"
        "nl" -> "Nederlands (nl)"
        "pl" -> "Polski (pl)"
        "ru" -> "Русский (ru)"
        else -> "$this ($this)"
    }
}

fun Int.toFormattedElo(): String {
    return when {
        this >= 1000 -> "${this / 1000}.${(this % 1000) / 100}k"
        else -> this.toString()
    }
}