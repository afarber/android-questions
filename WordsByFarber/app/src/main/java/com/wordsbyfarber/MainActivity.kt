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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
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
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var wordCount by remember { mutableIntStateOf(0) }
    //var words by remember { mutableStateOf(emptyList<Words>()) }

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
                            val database = Room.databaseBuilder(
                                context,
                                WordsDatabase::class.java, "${language}_database"
                            ).build()

                            // Check the count of words in the database
                            wordCount = database.wordsDao().countAll()
                            // if words count is low, (re)download them from /Consts-de.js
                            if (wordCount < 120000) {
                                database.wordsDao().deleteAll()
                                downloadAndParseJs(language, database.wordsDao())

                                // Update the count after inserting new words
                                wordCount = database.wordsDao().countAll()
                            }

                            isLoading = false
                            // Clear any previous error message
                            errorMessage = null
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                            // Store the error message
                            errorMessage = ex.message
                            isLoading = false
                        }
                    }
                }
            } else if (isLoading) {
                CircularProgressIndicator()
            } else if (errorMessage != null) {
                // Display the error message
                Text("Error: $errorMessage")
            } else {
                Text("Loaded! Word count: $wordCount")
                // TODO Display the words based on the selection
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
