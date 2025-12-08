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
import androidx.compose.ui.unit.dp
import com.wordsbyfarber.viewmodel.DictionaryViewModel
import com.wordsbyfarber.viewmodel.DownloadState
import java.text.NumberFormat

@Composable
fun LoadingDictionaryScreen(
    viewModel: DictionaryViewModel,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit,
    onBack: () -> Unit
) {
    val downloadState by viewModel.downloadState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.downloadDictionary()
    }

    LaunchedEffect(downloadState) {
        when (val state = downloadState) {
            is DownloadState.Success -> onSuccess()
            is DownloadState.Error -> onFailure(state.message)
            else -> { }
        }
    }

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
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val insertingState = downloadState as? DownloadState.Inserting
            val isDownloading = downloadState is DownloadState.Downloading
            val isSuccess = downloadState is DownloadState.Success

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(96.dp)
            ) {
                if (isDownloading) {
                    // Indeterminate spinner while downloading
                    CircularProgressIndicator(
                        modifier = Modifier.size(96.dp),
                        strokeWidth = 8.dp
                    )
                } else {
                    // Background track
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(96.dp),
                        strokeWidth = 8.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    // Animated progress (keep at 100% on success)
                    val animatedProgress by animateFloatAsState(
                        targetValue = when {
                            isSuccess -> 1f
                            insertingState != null -> insertingState.progress
                            else -> 0f
                        },
                        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                        label = "progress"
                    )

                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(96.dp),
                        strokeWidth = 8.dp,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    // Percentage text in center
                    if (insertingState != null || isSuccess) {
                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Text(
                text = if (isDownloading) "Downloading..." else "Inserting words...",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp)
            )

            viewModel.selectedLanguage?.let { language ->
                Text(
                    text = language.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Show word count progress
            insertingState?.let {
                val numberFormat = NumberFormat.getNumberInstance()
                Text(
                    text = "${numberFormat.format(it.wordsInserted)} words",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
