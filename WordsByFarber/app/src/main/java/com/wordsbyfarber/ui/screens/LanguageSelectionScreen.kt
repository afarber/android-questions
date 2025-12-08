package com.wordsbyfarber.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wordsbyfarber.R
import com.wordsbyfarber.data.model.Language
import com.wordsbyfarber.data.model.SupportedLanguages

/**
 * Initial screen where the user selects a dictionary language.
 *
 * Displays a scrollable list of available languages. Each language is shown
 * in a clickable card. When a language is selected, the app proceeds to
 * download that language's dictionary.
 *
 * @param onLanguageSelected Callback invoked when the user taps a language card,
 *                           passing the selected [Language] object
 */
@Composable
fun LanguageSelectionScreen(
    onLanguageSelected: (Language) -> Unit
) {
    Scaffold { innerPadding ->
        // LazyColumn efficiently renders only visible items
        // Good for long lists, though we only have 6 languages
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Header item (not part of the repeated items)
            item {
                Text(
                    text = stringResource(R.string.select_language),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // Generate a card for each supported language
            // SupportedLanguages.languages is a Map, so we convert values to List
            items(SupportedLanguages.languages.values.toList()) { language ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        // clickable modifier makes the entire card tappable
                        .clickable { onLanguageSelected(language) }
                ) {
                    // Display the language name (e.g., "Deutsch", "English")
                    Text(
                        text = language.name,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
