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
import androidx.compose.ui.unit.dp
import com.wordsbyfarber.viewmodel.DictionaryViewModel

@Composable
fun HomeScreen(
    viewModel: DictionaryViewModel,
    onBack: () -> Unit
) {
    val twoLetterCount by viewModel.getWordCount(2).collectAsState(initial = 0)
    val threeLetterCount by viewModel.getWordCount(3).collectAsState(initial = 0)

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = onBack,
                    icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") },
                    label = { Text("Back") }
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
            viewModel.selectedLanguage?.let { language ->
                Text(
                    text = language.name,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }

            Text(
                text = "2-letter words: $twoLetterCount",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = "3-letter words: $threeLetterCount",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}
