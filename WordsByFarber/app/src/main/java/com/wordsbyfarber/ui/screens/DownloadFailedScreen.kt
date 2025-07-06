package com.wordsbyfarber.ui.screens

// Screen 3: Download failed screen with retry option and error message
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wordsbyfarber.R
import com.wordsbyfarber.ui.components.AppTopBar
import com.wordsbyfarber.ui.components.LoadingIndicator
import com.wordsbyfarber.ui.theme.WordsByFarberTheme
import com.wordsbyfarber.ui.viewmodels.DownloadFailedViewModel
import com.wordsbyfarber.ui.viewmodels.DownloadFailedUiState

@Composable
fun DownloadFailedScreen(
    viewModel: DownloadFailedViewModel,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle navigation
    LaunchedEffect(uiState.shouldNavigateToLoading) {
        if (uiState.shouldNavigateToLoading) {
            onRetry()
            viewModel.clearNavigationState()
        }
    }

    LaunchedEffect(uiState.shouldNavigateToLanguageSelection) {
        if (uiState.shouldNavigateToLanguageSelection) {
            onCancel()
            viewModel.clearNavigationState()
        }
    }

    // Handle errors
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.download_failed),
                onCloseClick = { viewModel.close() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        DownloadFailedContent(
            uiState = uiState,
            onRetry = { viewModel.retry() },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun DownloadFailedContent(
    uiState: DownloadFailedUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Error icon
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = "Download failed",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Error title
            Text(
                text = stringResource(R.string.download_failed_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error message
            Text(
                text = stringResource(R.string.download_failed_message),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Retry button
            Button(
                onClick = onRetry,
                enabled = !uiState.isRetrying,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isRetrying) {
                    LoadingIndicator(
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = if (uiState.isRetrying) {
                        stringResource(R.string.retrying)
                    } else {
                        stringResource(R.string.retry)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DownloadFailedScreenPreview() {
    WordsByFarberTheme {
        DownloadFailedContent(
            uiState = DownloadFailedUiState(
                isRetrying = false
            ),
            onRetry = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DownloadFailedScreenRetryingPreview() {
    WordsByFarberTheme {
        DownloadFailedContent(
            uiState = DownloadFailedUiState(
                isRetrying = true
            ),
            onRetry = { }
        )
    }
}