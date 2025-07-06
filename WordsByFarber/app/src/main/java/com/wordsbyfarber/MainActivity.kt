package com.wordsbyfarber

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wordsbyfarber.ui.theme.WordsByFarberTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var hiltValidator: HiltDependencyValidation
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordsByFarberTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HiltStatusScreen(
                        hiltValidator = hiltValidator,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun HiltStatusScreen(
    hiltValidator: HiltDependencyValidation,
    modifier: Modifier = Modifier
) {
    var validationResult by remember { mutableStateOf<HiltValidationResult?>(null) }
    
    LaunchedEffect(Unit) {
        validationResult = hiltValidator.validateDependencies()
    }
    
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = "Hilt Dependency Injection Status",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        validationResult?.let { result ->
            Text(
                text = if (result.success) "✅ SUCCESS" else "❌ FAILED",
                style = MaterialTheme.typography.titleLarge,
                color = if (result.success) Color(0xFF4CAF50) else Color(0xFFF44336),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Dictionary Repository: ${if (result.dictionaryRepositoryWorking) "✅" else "❌"}",
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Text(
                text = "Preferences Repository: ${if (result.preferencesRepositoryWorking) "✅" else "❌"}",
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Text(
                text = "Network Utils: ${if (result.networkUtilsWorking) "✅" else "❌"}",
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Text(
                text = "String Utils: ${if (result.stringUtilsWorking) "✅" else "❌"}",
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = result.details,
                style = MaterialTheme.typography.bodyMedium
            )
        } ?: run {
            Text(
                text = "Validating dependencies...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HiltStatusScreenPreview() {
    WordsByFarberTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Preview - Hilt validation would run in actual app")
        }
    }
}