package de.afarber.magicapp.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import de.afarber.magicapp.BuildConfig

@Composable
fun MagicInfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { Text("Magic App ${BuildConfig.VERSION_NAME}") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
    )
}
