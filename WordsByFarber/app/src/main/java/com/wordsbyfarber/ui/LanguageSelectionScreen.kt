package com.wordsbyfarber.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun LanguageSelectionScreen(onLanguageSelected: (String) -> Unit) {
    val languages = listOf("de", "en", "fr", "nl", "pl", "ru")
    Column {
        Text("Select a Language")
        languages.forEach { language ->
            Button(onClick = { onLanguageSelected(language) }) {
                Text(language)
            }
        }
    }
}
