package com.wordsbyfarber.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wordsbyfarber.R
import com.wordsbyfarber.viewmodel.DictionaryViewModel
import com.wordsbyfarber.viewmodel.DownloadState
import java.text.NumberFormat

/**
 * Screen that displays download and database insertion progress.
 *
 * This screen shows two different states:
 * 1. Downloading - indeterminate spinner while fetching the dictionary file
 * 2. Inserting - determinate progress bar while inserting words into the database
 *
 * @param viewModel The ViewModel that manages dictionary download state
 * @param onSuccess Callback invoked when download and insertion complete successfully
 * @param onFailure Callback invoked when an error occurs, with the error message
 * @param onBack Callback invoked when the user presses the back button
 */
@Composable
fun LoadingDictionaryScreen(
    viewModel: DictionaryViewModel,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit,
    onBack: () -> Unit
) {
    // Collect the current download state from the ViewModel
    val downloadState by viewModel.downloadState.collectAsState()

    // Start the download when this screen first appears
    // LaunchedEffect(Unit) runs once when the composable enters the composition
    LaunchedEffect(Unit) {
        viewModel.downloadDictionary()
    }

    // React to state changes and navigate accordingly
    // This effect re-runs whenever downloadState changes
    LaunchedEffect(downloadState) {
        when (val state = downloadState) {
            is DownloadState.Success -> onSuccess()
            is DownloadState.Error -> onFailure(state.message)
            // Idle, Downloading, and Inserting states don't trigger navigation
            else -> { }
        }
    }

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
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Smart cast to Inserting state if applicable
            val insertingState = downloadState as? DownloadState.Inserting
            val isDownloading = downloadState is DownloadState.Downloading
            val isSuccess = downloadState is DownloadState.Success

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(96.dp)
            ) {
                if (isDownloading) {
                    // Indeterminate spinner while downloading the JS file
                    // No progress parameter = spinning animation
                    CircularProgressIndicator(
                        modifier = Modifier.size(96.dp),
                        strokeWidth = 8.dp
                    )
                } else {
                    // Background track (full circle in subtle color)
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(96.dp),
                        strokeWidth = 8.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    // Animate progress changes smoothly
                    // Keep at 100% on success to prevent animation back to 0
                    val animatedProgress by animateFloatAsState(
                        targetValue = when {
                            isSuccess -> 1f
                            insertingState != null -> insertingState.progress
                            else -> 0f
                        },
                        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                        label = "progress"
                    )

                    // Foreground progress arc
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(96.dp),
                        strokeWidth = 8.dp,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    // Percentage text in center of the circle
                    if (insertingState != null || isSuccess) {
                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Status text changes based on current phase
            Text(
                text = stringResource(
                    if (isDownloading) R.string.downloading else R.string.inserting_words
                ),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp)
            )

            // Display the selected language name
            viewModel.selectedLanguage?.let { language ->
                Text(
                    text = language.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Show word count progress during insertion phase
            insertingState?.let {
                // Format number with locale-appropriate separators (e.g., 1,234,567)
                val numberFormat = NumberFormat.getNumberInstance()
                Text(
                    text = stringResource(
                        R.string.words_count,
                        numberFormat.format(it.wordsInserted)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
