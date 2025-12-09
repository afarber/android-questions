package com.wordsbyfarber.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wordsbyfarber.R
import com.wordsbyfarber.viewmodel.DictionaryViewModel

/**
 * Main screen displayed after a dictionary is successfully loaded.
 *
 * Shows statistics about the loaded dictionary, including counts of
 * 2-letter and 3-letter words. The word counts are loaded from the
 * Room database and update reactively via Flow.
 *
 * @param viewModel The ViewModel that provides access to dictionary data
 * @param onBack Callback invoked when the user presses the back button,
 *               typically navigates back to language selection
 */
@Composable
fun HomeScreen(
    viewModel: DictionaryViewModel,
    onBack: () -> Unit
) {
    // Collect word counts from the database as State
    // initial = 0 provides a default while the Flow hasn't emitted yet
    val twoLetterCount by viewModel.getWordCount(2).collectAsState(initial = 0)
    val threeLetterCount by viewModel.getWordCount(3).collectAsState(initial = 0)

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = onBack,
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    },
                    label = { Text(stringResource(R.string.back)) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Display the selected language name as a header
            Text(
                text = viewModel.selectedLanguage.name,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Word count statistics
            // stringResource with format arguments: %1$d is replaced by the count
            Text(
                text = stringResource(R.string.two_letter_words, twoLetterCount),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = stringResource(R.string.three_letter_words, threeLetterCount),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}
