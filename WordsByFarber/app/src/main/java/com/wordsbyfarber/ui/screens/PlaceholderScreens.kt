package com.wordsbyfarber.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wordsbyfarber.ui.components.AppTopBar

@Composable
fun TopPlayersScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLanguageSelection: () -> Unit,
    viewModel: Any = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Top Players",
                onCloseClick = onNavigateToHome
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Top Players functionality will be implemented here.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLanguageSelection: () -> Unit,
    viewModel: Any = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Your Profile",
                onCloseClick = onNavigateToHome
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "User profile functionality will be implemented here.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RareLetter1Screen(
    onNavigateToHome: () -> Unit,
    onNavigateToLanguageSelection: () -> Unit,
    viewModel: com.wordsbyfarber.ui.viewmodels.WordListViewModel = hiltViewModel()
) {
    RareLetter1WordsScreen(
        onNavigateToHome = onNavigateToHome,
        onNavigateToLanguageSelection = onNavigateToLanguageSelection,
        viewModel = viewModel
    )
}

@Composable
fun RareLetter2Screen(
    onNavigateToHome: () -> Unit,
    onNavigateToLanguageSelection: () -> Unit,
    viewModel: com.wordsbyfarber.ui.viewmodels.WordListViewModel = hiltViewModel()
) {
    RareLetter2WordsScreen(
        onNavigateToHome = onNavigateToHome,
        onNavigateToLanguageSelection = onNavigateToLanguageSelection,
        viewModel = viewModel
    )
}

@Composable
fun HelpScreen(
    onNavigateToHome: () -> Unit,
    viewModel: Any = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Help",
                onCloseClick = onNavigateToHome
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Help content will be loaded here.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Composable
fun PrivacyPolicyScreen(
    onNavigateToHome: () -> Unit,
    viewModel: Any = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Privacy Policy",
                onCloseClick = onNavigateToHome
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Privacy policy content will be loaded here.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Composable
fun TermsOfServiceScreen(
    onNavigateToHome: () -> Unit,
    viewModel: Any = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Terms of Service",
                onCloseClick = onNavigateToHome
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Terms of service content will be loaded here.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Justify
            )
        }
    }
}