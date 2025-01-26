package com.wordsbyfarber.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wordsbyfarber.utils.getFlagEmoji

@Composable
fun LanguageSelectionScreen(onLanguageSelected: (String) -> Unit) {
    val languages = listOf("de", "en", "fr", "nl", "pl", "ru")
    Column(modifier = Modifier
        .padding(all = 8.dp)
        // Add padding to avoid overlap with status bar
        .statusBarsPadding()
    ) {
        Text("Select a Language")
        Spacer(modifier = Modifier.height(4.dp))
        languages.forEach { language ->
            Button(
                onClick = { onLanguageSelected(language) },
                // Make button occupy full width
                modifier = Modifier.fillMaxWidth()
            ) {
                val flagEmoji = getFlagEmoji(language.uppercase())
                Text("$flagEmoji $language")
            }
            // Add space between buttons
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
