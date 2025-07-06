package com.wordsbyfarber.ui.components

// Loading indicator components supporting both determinate and indeterminate progress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wordsbyfarber.ui.theme.WordsByFarberTheme

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    message: String? = null,
    progress: Float? = null,
    maxProgress: Int? = null,
    currentProgress: Int? = null
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (progress != null) {
                // Determinate progress indicator
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.size(64.dp),
                    strokeWidth = 6.dp,
                    strokeCap = StrokeCap.Round,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                
                if (maxProgress != null && currentProgress != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$currentProgress / $maxProgress",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Indeterminate progress indicator
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    strokeWidth = 6.dp,
                    strokeCap = StrokeCap.Round,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            if (message != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
    }
}

@Composable
fun LinearLoadingIndicator(
    progress: Float? = null,
    message: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(16.dp)
    ) {
        if (message != null) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        if (progress != null) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxSize()
                    .height(8.dp),
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        } else {
            LinearProgressIndicator(
                modifier = Modifier
                    .width(200.dp)
                    .height(8.dp),
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SmallLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Int = 24
) {
    CircularProgressIndicator(
        modifier = modifier.size(size.dp),
        strokeWidth = 3.dp,
        strokeCap = StrokeCap.Round,
        color = MaterialTheme.colorScheme.primary
    )
}

@Preview(showBackground = true)
@Composable
fun LoadingIndicatorPreview() {
    WordsByFarberTheme {
        LoadingIndicator(
            message = "Loading dictionary...",
            modifier = Modifier.size(200.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingIndicatorWithProgressPreview() {
    WordsByFarberTheme {
        LoadingIndicator(
            message = "Parsing dictionary...",
            progress = 0.7f,
            currentProgress = 70000,
            maxProgress = 100000,
            modifier = Modifier.size(200.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LinearLoadingIndicatorPreview() {
    WordsByFarberTheme {
        LinearLoadingIndicator(
            progress = 0.5f,
            message = "Downloading...",
            modifier = Modifier.width(300.dp)
        )
    }
}