package com.wordsbyfarber.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wordsbyfarber.R
import com.wordsbyfarber.ui.components.AppTopBar
import com.wordsbyfarber.ui.theme.WordsByFarberTheme
import com.wordsbyfarber.ui.viewmodels.LoadingDictionaryUiState

@Composable
fun LoadingDictionaryScreen(
    uiState: LoadingDictionaryUiState,
    onCancel: () -> Unit,
    onDownloadComplete: () -> Unit,
    onDownloadFailed: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Handle navigation
    LaunchedEffect(uiState.shouldNavigateToHome) {
        if (uiState.shouldNavigateToHome) {
            onDownloadComplete()
        }
    }

    LaunchedEffect(uiState.shouldNavigateToError) {
        if (uiState.shouldNavigateToError) {
            onDownloadFailed()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.loading_dictionary),
                onCloseClick = onCancel
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LoadingDictionaryContent(
            uiState = uiState,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun LoadingDictionaryContent(
    uiState: LoadingDictionaryUiState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Main progress indicator
            if (uiState.progress > 0) {
                LinearProgressIndicator(
                    progress = { uiState.progress / 100f },
                    modifier = Modifier.width(200.dp)
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Status message
            if (uiState.statusMessage.isNotBlank()) {
                Text(
                    text = uiState.statusMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }

            // Progress information
            if (uiState.progress > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${uiState.progress}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Word count if available
            if (uiState.wordCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${uiState.wordCount} words loaded",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingDictionaryScreenPreview() {
    WordsByFarberTheme {
        LoadingDictionaryContent(
            uiState = LoadingDictionaryUiState(
                isLoading = true,
                progress = 65,
                statusMessage = "Parsing dictionary...",
                wordCount = 150000
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingDictionaryScreenIndeterminatePreview() {
    WordsByFarberTheme {
        LoadingDictionaryContent(
            uiState = LoadingDictionaryUiState(
                isLoading = true,
                progress = 0,
                statusMessage = "Starting download..."
            )
        )
    }
}