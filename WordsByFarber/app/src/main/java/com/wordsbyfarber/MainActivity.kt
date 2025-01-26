package com.wordsbyfarber

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.wordsbyfarber.data.Words
import com.wordsbyfarber.data.WordsDatabase
import com.wordsbyfarber.network.downloadAndParseJs
import com.wordsbyfarber.ui.LanguageSelectionScreen
import com.wordsbyfarber.ui.theme.WordsByFarberTheme
import com.wordsbyfarber.utils.saveLanguage
import kotlinx.coroutines.launch

@Composable
fun MyApp() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var language by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var words by remember { mutableStateOf(emptyList<Words>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) } // New state for error messages

    Box(
        // Fill the entire screen
        modifier = Modifier.fillMaxSize(),
        // Center the content within the Box
        contentAlignment = Alignment.Center
    ) {
        Column {
            if (language.isEmpty()) {
                LanguageSelectionScreen { selectedLanguage ->
                    language = selectedLanguage
                    saveLanguage(context, language)
                    isLoading = true

                    coroutineScope.launch {
                        try {
                            val jsonData = downloadAndParseJs(language)
                            val wordList = jsonData.map { Words(it.key, it.value) }

                            val database = Room.databaseBuilder(
                                context,
                                WordsDatabase::class.java, "${language}_database"
                            ).build()
                            database.wordsDao().deleteAll()
                            database.wordsDao().insertAll(wordList)
                            isLoading = false
                            words = wordList
                            errorMessage = null // Clear any previous error message
                        } catch (e: Exception) {
                            e.printStackTrace()
                            errorMessage = e.message // Store the error message
                            isLoading = false
                        }
                    }
                }
            } else if (isLoading) {
                CircularProgressIndicator()
            } else if (errorMessage != null) {
                Text("Error: $errorMessage") // Display the error message
            } else {
                Text("Loaded!")
                // Display the words based on the selection
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WordsByFarberTheme {
                MyApp()
            }
        }
    }
}

@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun PreviewMyApp() {
    WordsByFarberTheme {
        LanguageSelectionScreen {}
    }
}
