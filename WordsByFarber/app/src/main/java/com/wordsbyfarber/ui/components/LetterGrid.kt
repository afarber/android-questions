package com.wordsbyfarber.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordsbyfarber.ui.theme.WordsByFarberTheme

@Composable
fun LetterGrid(
    grid: List<List<String>>,
    modifier: Modifier = Modifier,
    onCellClick: ((row: Int, col: Int) -> Unit)? = null
) {
    val gridSize = grid.size
    val cellSize = when {
        gridSize <= 5 -> 56.dp
        gridSize <= 10 -> 32.dp
        else -> 24.dp
    }
    
    val fontSize = when {
        gridSize <= 5 -> 20.sp
        gridSize <= 10 -> 16.sp
        else -> 12.sp
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        grid.forEachIndexed { rowIndex, row ->
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEachIndexed { colIndex, letter ->
                    LetterCell(
                        letter = letter,
                        size = cellSize,
                        fontSize = fontSize,
                        onClick = if (onCellClick != null) {
                            { onCellClick(rowIndex, colIndex) }
                        } else null
                    )
                }
            }
        }
    }
}

@Composable
private fun LetterCell(
    letter: String,
    size: androidx.compose.ui.unit.Dp,
    fontSize: androidx.compose.ui.unit.TextUnit,
    onClick: (() -> Unit)? = null,
    isSelected: Boolean = false
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .padding(1.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(4.dp)
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
    ) {
        Text(
            text = letter,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
fun StaticLetterGrid5x5(
    modifier: Modifier = Modifier
) {
    val staticGrid = listOf(
        listOf("A", "B", "C", "D", "E"),
        listOf("F", "G", "H", "I", "J"),
        listOf("K", "L", "M", "N", "O"),
        listOf("P", "Q", "R", "S", "T"),
        listOf("U", "V", "W", "X", "Y")
    )
    
    LetterGrid(
        grid = staticGrid,
        modifier = modifier
    )
}

@Composable
fun StaticLetterGrid15x15(
    modifier: Modifier = Modifier
) {
    val staticGrid = listOf(
        listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O"),
        listOf("P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "A", "B", "C", "D"),
        listOf("E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S"),
        listOf("T", "U", "V", "W", "X", "Y", "Z", "A", "B", "C", "D", "E", "F", "G", "H"),
        listOf("I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W"),
        listOf("X", "Y", "Z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"),
        listOf("M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "A"),
        listOf("B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P"),
        listOf("Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "A", "B", "C", "D", "E"),
        listOf("F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T"),
        listOf("U", "V", "W", "X", "Y", "Z", "A", "B", "C", "D", "E", "F", "G", "H", "I"),
        listOf("J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X"),
        listOf("Y", "Z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"),
        listOf("N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "A", "B"),
        listOf("C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q")
    )
    
    LetterGrid(
        grid = staticGrid,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun LetterGrid5x5Preview() {
    WordsByFarberTheme {
        StaticLetterGrid5x5(
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LetterGrid15x15Preview() {
    WordsByFarberTheme {
        StaticLetterGrid15x15(
            modifier = Modifier.padding(16.dp)
        )
    }
}