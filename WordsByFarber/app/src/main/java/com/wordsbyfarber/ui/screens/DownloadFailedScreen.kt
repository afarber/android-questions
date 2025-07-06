package com.wordsbyfarber.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordsbyfarber.ui.components.AppTopBar
import com.wordsbyfarber.ui.components.ErrorIndicator
import com.wordsbyfarber.ui.theme.WordsByFarberTheme
import com.wordsbyfarber.ui.viewmodels.DownloadFailedUiState
import com.wordsbyfarber.ui.viewmodels.DownloadFailedViewModel

@Composable
fun DownloadFailedScreen(
    onNavigateToLoading: () -> Unit,
    onNavigateToLanguageSelection: () -> Unit,
    viewModel: DownloadFailedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.shouldNavigateToLoading) {
        if (uiState.shouldNavigateToLoading) {
            viewModel.clearNavigationState()
            onNavigateToLoading()
        }
    }

    LaunchedEffect(uiState.shouldNavigateToLanguageSelection) {
        if (uiState.shouldNavigateToLanguageSelection) {
            viewModel.clearNavigationState()
            onNavigateToLanguageSelection()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    DownloadFailedContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onRetryClick = {
            viewModel.retry()
        },
        onCloseClick = {
            viewModel.close()
        }
    )
}

@Composable
private fun DownloadFailedContent(
    uiState: DownloadFailedUiState,
    snackbarHostState: SnackbarHostState,
    onRetryClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Download Failed",
                onCloseClick = onCloseClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                ErrorIndicator(
                    message = "Download Failed",
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                
                Text(
                    text = "Please check your internet connection and try again.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                
                Button(
                    onClick = onRetryClick,
                    enabled = !uiState.isRetrying,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (uiState.isRetrying) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Retry Download")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = onCloseClick,
                    enabled = !uiState.isRetrying,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Cancel")
                }
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
            snackbarHostState = SnackbarHostState(),
            onRetryClick = {},
            onCloseClick = {}
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
            snackbarHostState = SnackbarHostState(),
            onRetryClick = {},
            onCloseClick = {}
        )
    }
}