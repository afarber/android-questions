package com.wordsbyfarber.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wordsbyfarber.R

/**
 * Error screen displayed when dictionary download fails.
 *
 * Shows the error message and provides two navigation options:
 * - Back: Return to language selection to choose a different language
 * - Retry: Attempt to download the same dictionary again
 *
 * @param errorMessage The error message to display, typically from the exception
 * @param onRetry Callback invoked when the user taps the Retry button
 * @param onBack Callback invoked when the user taps the Back button
 */
@Composable
fun FailedDownloadScreen(
    errorMessage: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        bottomBar = {
            // Bottom navigation with two actions
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
                NavigationBarItem(
                    selected = false,
                    onClick = onRetry,
                    icon = {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.retry)
                        )
                    },
                    label = { Text(stringResource(R.string.retry)) }
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
            // Error title in red/error color
            Text(
                text = stringResource(R.string.download_failed),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Detailed error message
            // textAlign = Center ensures multi-line messages are centered
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
