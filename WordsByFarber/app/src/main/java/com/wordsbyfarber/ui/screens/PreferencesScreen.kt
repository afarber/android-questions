package com.wordsbyfarber.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordsbyfarber.ui.components.AppTopBar
import com.wordsbyfarber.ui.components.LoadingIndicator
import com.wordsbyfarber.ui.theme.WordsByFarberTheme
import com.wordsbyfarber.ui.viewmodels.PreferenceItem
import com.wordsbyfarber.ui.viewmodels.PreferencesUiState
import com.wordsbyfarber.ui.viewmodels.PreferencesViewModel

@Composable
fun PreferencesScreen(
    onNavigateToHome: () -> Unit,
    viewModel: PreferencesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.shouldNavigateToHome) {
        if (uiState.shouldNavigateToHome) {
            viewModel.clearNavigationState()
            onNavigateToHome()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    PreferencesContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onPreferenceToggle = { preferenceId ->
            viewModel.togglePreference(preferenceId)
        },
        onCloseClick = {
            viewModel.close()
        }
    )
}

@Composable
private fun PreferencesContent(
    uiState: PreferencesUiState,
    snackbarHostState: SnackbarHostState,
    onPreferenceToggle: (String) -> Unit,
    onCloseClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Preferences",
                onCloseClick = onCloseClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.preferenceItems.isEmpty()) {
                LoadingIndicator(
                    message = "Loading preferences...",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.preferenceItems,
                        key = { it.id }
                    ) { preference ->
                        PreferenceItem(
                            preference = preference,
                            onToggle = { onPreferenceToggle(preference.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PreferenceItem(
    preference: PreferenceItem,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = preference.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = preference.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Switch(
                checked = preference.isEnabled,
                onCheckedChange = { onToggle() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreferencesScreenPreview() {
    WordsByFarberTheme {
        PreferencesContent(
            uiState = PreferencesUiState(
                currentLanguage = "en",
                preferenceItems = listOf(
                    PreferenceItem(
                        id = "sound_effects",
                        title = "Sound Effects",
                        description = "Enable sound effects during games",
                        isEnabled = true
                    ),
                    PreferenceItem(
                        id = "auto_save",
                        title = "Auto Save",
                        description = "Automatically save game progress",
                        isEnabled = true
                    ),
                    PreferenceItem(
                        id = "dark_theme",
                        title = "Dark Theme",
                        description = "Use dark theme for better visibility",
                        isEnabled = false
                    ),
                    PreferenceItem(
                        id = "notifications",
                        title = "Push Notifications",
                        description = "Receive notifications about new features",
                        isEnabled = true
                    )
                )
            ),
            snackbarHostState = SnackbarHostState(),
            onPreferenceToggle = {},
            onCloseClick = {}
        )
    }
}