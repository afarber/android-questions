package de.afarber.magicapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun magicCard(
    title: String,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
    onReloadClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val cardBorder = accentColor().copy(alpha = 0.9f)
    val iconBackground = accentColor().copy(alpha = 0.12f)

    OutlinedCard(
        modifier = modifier,
        border = BorderStroke(1.dp, cardBorder),
        colors =
            CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    circleActionButton(onClick = onInfoClick, container = iconBackground) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Info",
                            tint = accentColor(),
                            modifier = Modifier.size(16.dp),
                        )
                    }

                    if (onReloadClick != null) {
                        circleActionButton(onClick = onReloadClick, container = iconBackground) {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = "Reload",
                                tint = accentColor(),
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = accentColor().copy(alpha = 0.25f))
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun circleActionButton(
    onClick: () -> Unit,
    container: Color,
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(container)
                .border(
                    width = 1.dp,
                    color = accentColor().copy(alpha = 0.6f),
                    shape = CircleShape,
                ).clickable(onClick = onClick)
                .padding(5.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
fun accentColor(): Color = MaterialTheme.colorScheme.primary
