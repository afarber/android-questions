package de.afarber.magicapp.ui.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.afarber.magicapp.ui.components.MagicInfoDialog
import de.afarber.magicapp.ui.components.MagicNavPanel
import de.afarber.magicapp.ui.components.accentColor
import de.afarber.magicapp.ui.components.isWideScreen
import de.afarber.magicapp.ui.navigation.MenuSection
import de.afarber.magicapp.ui.screens.SectionContent
import kotlinx.coroutines.launch

@Composable
fun MagicAppRoot() {
    var selectedSection by rememberSaveable { mutableStateOf(MenuSection.Connectivity) }
    var showInfoDialog by rememberSaveable { mutableStateOf(false) }

    if (showInfoDialog) {
        MagicInfoDialog(onDismiss = { showInfoDialog = false })
    }

    if (isWideScreen(900)) {
        desktopLayout(
            selectedSection = selectedSection,
            onSectionSelected = { selectedSection = it },
            onInfoClick = { showInfoDialog = true },
        )
    } else {
        compactLayout(
            selectedSection = selectedSection,
            onSectionSelected = { selectedSection = it },
            onInfoClick = { showInfoDialog = true },
        )
    }
}

@Composable
private fun desktopLayout(
    selectedSection: MenuSection,
    onSectionSelected: (MenuSection) -> Unit,
    onInfoClick: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        MagicNavPanel(
            selectedSection = selectedSection,
            onSectionSelected = onSectionSelected,
            logoPulseTriggerKey = selectedSection.ordinal,
            modifier =
                Modifier
                    .width(220.dp)
                    .fillMaxHeight(),
        )
        VerticalDivider(color = accentColor().copy(alpha = 0.35f))
        Box(modifier = Modifier.fillMaxSize()) {
            SectionContent(
                selectedSection = selectedSection,
                onInfoClick = onInfoClick,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun compactLayout(
    selectedSection: MenuSection,
    onSectionSelected: (MenuSection) -> Unit,
    onInfoClick: () -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var logoPulseTriggerKey by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(drawerState.currentValue) {
        if (drawerState.currentValue == DrawerValue.Open) {
            logoPulseTriggerKey += 1
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                MagicNavPanel(
                    selectedSection = selectedSection,
                    onSectionSelected = {
                        onSectionSelected(it)
                        scope.launch { drawerState.close() }
                    },
                    logoPulseTriggerKey = logoPulseTriggerKey,
                    modifier = Modifier.fillMaxHeight(),
                )
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = selectedSection.label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Rounded.Menu, contentDescription = "Open menu")
                        }
                    },
                )
            },
        ) { padding ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
            ) {
                SectionContent(selectedSection = selectedSection, onInfoClick = onInfoClick)
            }
        }
    }
}
