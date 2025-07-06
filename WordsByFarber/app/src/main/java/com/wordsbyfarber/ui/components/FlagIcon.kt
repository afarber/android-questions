package com.wordsbyfarber.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wordsbyfarber.ui.theme.WordsByFarberTheme

@Composable
fun FlagIcon(
    languageCode: String,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 4.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                RoundedCornerShape(cornerRadius)
            )
    ) {
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            when (languageCode.lowercase()) {
                "de" -> drawGermanFlag(size)
                "en" -> drawBritishFlag(size)
                "fr" -> drawFrenchFlag(size)
                "nl" -> drawDutchFlag(size)
                "pl" -> drawPolishFlag(size)
                "ru" -> drawRussianFlag(size)
                else -> drawDefaultFlag(size)
            }
        }
    }
}

private fun DrawScope.drawGermanFlag(size: Size) {
    val stripHeight = size.height / 3
    
    // Black stripe
    drawRect(
        color = Color.Black,
        topLeft = Offset(0f, 0f),
        size = Size(size.width, stripHeight)
    )
    
    // Red stripe
    drawRect(
        color = Color.Red,
        topLeft = Offset(0f, stripHeight),
        size = Size(size.width, stripHeight)
    )
    
    // Gold stripe
    drawRect(
        color = Color(0xFFFFD700),
        topLeft = Offset(0f, stripHeight * 2),
        size = Size(size.width, stripHeight)
    )
}

private fun DrawScope.drawBritishFlag(size: Size) {
    // Blue background
    drawRect(
        color = Color(0xFF012169),
        topLeft = Offset(0f, 0f),
        size = size
    )
    
    // White diagonal cross
    val strokeWidth = size.width * 0.08f
    
    // Main white cross
    drawRect(
        color = Color.White,
        topLeft = Offset((size.width - strokeWidth) / 2, 0f),
        size = Size(strokeWidth, size.height)
    )
    
    drawRect(
        color = Color.White,
        topLeft = Offset(0f, (size.height - strokeWidth) / 2),
        size = Size(size.width, strokeWidth)
    )
    
    // Red cross (smaller)
    val redStrokeWidth = strokeWidth * 0.6f
    drawRect(
        color = Color.Red,
        topLeft = Offset((size.width - redStrokeWidth) / 2, 0f),
        size = Size(redStrokeWidth, size.height)
    )
    
    drawRect(
        color = Color.Red,
        topLeft = Offset(0f, (size.height - redStrokeWidth) / 2),
        size = Size(size.width, redStrokeWidth)
    )
}

private fun DrawScope.drawFrenchFlag(size: Size) {
    val stripWidth = size.width / 3
    
    // Blue stripe
    drawRect(
        color = Color(0xFF0055A4),
        topLeft = Offset(0f, 0f),
        size = Size(stripWidth, size.height)
    )
    
    // White stripe
    drawRect(
        color = Color.White,
        topLeft = Offset(stripWidth, 0f),
        size = Size(stripWidth, size.height)
    )
    
    // Red stripe
    drawRect(
        color = Color(0xFFEF4135),
        topLeft = Offset(stripWidth * 2, 0f),
        size = Size(stripWidth, size.height)
    )
}

private fun DrawScope.drawDutchFlag(size: Size) {
    val stripHeight = size.height / 3
    
    // Red stripe
    drawRect(
        color = Color(0xFFAE1C28),
        topLeft = Offset(0f, 0f),
        size = Size(size.width, stripHeight)
    )
    
    // White stripe
    drawRect(
        color = Color.White,
        topLeft = Offset(0f, stripHeight),
        size = Size(size.width, stripHeight)
    )
    
    // Blue stripe
    drawRect(
        color = Color(0xFF21468B),
        topLeft = Offset(0f, stripHeight * 2),
        size = Size(size.width, stripHeight)
    )
}

private fun DrawScope.drawPolishFlag(size: Size) {
    val stripHeight = size.height / 2
    
    // White stripe
    drawRect(
        color = Color.White,
        topLeft = Offset(0f, 0f),
        size = Size(size.width, stripHeight)
    )
    
    // Red stripe
    drawRect(
        color = Color(0xFFDC143C),
        topLeft = Offset(0f, stripHeight),
        size = Size(size.width, stripHeight)
    )
}

private fun DrawScope.drawRussianFlag(size: Size) {
    val stripHeight = size.height / 3
    
    // White stripe
    drawRect(
        color = Color.White,
        topLeft = Offset(0f, 0f),
        size = Size(size.width, stripHeight)
    )
    
    // Blue stripe
    drawRect(
        color = Color(0xFF0039A6),
        topLeft = Offset(0f, stripHeight),
        size = Size(size.width, stripHeight)
    )
    
    // Red stripe
    drawRect(
        color = Color(0xFFD52B1E),
        topLeft = Offset(0f, stripHeight * 2),
        size = Size(size.width, stripHeight)
    )
}

private fun DrawScope.drawDefaultFlag(size: Size) {
    // Gray background for unknown flags
    drawRect(
        color = Color.Gray,
        topLeft = Offset(0f, 0f),
        size = size
    )
    
    // White question mark
    drawRect(
        color = Color.White,
        topLeft = Offset(size.width * 0.4f, size.height * 0.2f),
        size = Size(size.width * 0.2f, size.height * 0.6f)
    )
}

@Preview(showBackground = true)
@Composable
fun FlagIconPreview() {
    WordsByFarberTheme {
        androidx.compose.foundation.layout.Row {
            FlagIcon("de", Modifier.size(48.dp))
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
            FlagIcon("en", Modifier.size(48.dp))
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
            FlagIcon("fr", Modifier.size(48.dp))
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
            FlagIcon("nl", Modifier.size(48.dp))
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
            FlagIcon("pl", Modifier.size(48.dp))
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
            FlagIcon("ru", Modifier.size(48.dp))
        }
    }
}