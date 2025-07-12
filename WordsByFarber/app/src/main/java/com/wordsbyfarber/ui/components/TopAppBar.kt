package com.wordsbyfarber.ui.components

// Reusable top app bar components with close buttons and language selection
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wordsbyfarber.ui.theme.WordsByFarberTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onCloseClick: (() -> Unit)? = null,
    leadingIcon: ImageVector? = null,
    onLeadingIconClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (leadingIcon != null && onLeadingIconClick != null) {
                IconButton(onClick = onLeadingIconClick) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = "Navigate back"
                    )
                }
            }
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                trailingContent?.invoke()
                
                if (onCloseClick != null) {
                    IconButton(onClick = onCloseClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun HomeTopBar(
    title: String,
    languageCode: String,
    onLanguageClick: () -> Unit
) {
    AppTopBar(
        title = title,
        trailingContent = {
            LanguageButton(
                languageCode = languageCode,
                onClick = onLanguageClick
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    )
}

@Composable
fun LanguageButton(
    languageCode: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .padding(4.dp)
        ) {
            FlagIcon(
                languageCode = languageCode,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppTopBarPreview() {
    WordsByFarberTheme {
        AppTopBar(
            title = "Find a Word",
            onCloseClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeTopBarPreview() {
    WordsByFarberTheme {
        HomeTopBar(
            title = "Words by Farber",
            languageCode = "en",
            onLanguageClick = {}
        )
    }
}