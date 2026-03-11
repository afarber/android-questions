package de.afarber.MagicApp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.afarber.MagicApp.ui.navigation.MenuSection

@Composable
fun MagicNavPanel(
    selectedSection: MenuSection,
    onSectionSelected: (MenuSection) -> Unit,
    modifier: Modifier = Modifier
) {
    val dark = isSystemInDarkTheme()
    val navBackground = if (dark) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    } else {
        Color(0xFFE9ECEB)
    }

    Column(
        modifier = modifier
            .background(navBackground)
            .padding(horizontal = 10.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BoxLogo()
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "MAGIC\nEngineering\nMenu",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.size(18.dp))

        MenuSection.entries.forEach { section ->
            val selected = selectedSection == section
            val background = if (selected) accentColor() else Color.Transparent
            val contentColor = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(background)
                    .clickable { onSectionSelected(section) }
                    .padding(horizontal = 12.dp, vertical = 9.dp)
            ) {
                Text(
                    text = section.label,
                    color = contentColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun BoxLogo() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(accentColor().copy(alpha = 0.25f))
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(accentColor())
        )
    }
}
