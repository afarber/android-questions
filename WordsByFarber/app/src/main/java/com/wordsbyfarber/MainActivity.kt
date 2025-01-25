package com.wordsbyfarber

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.wordsbyfarber.data.WordDatabase
import com.wordsbyfarber.data.Words
import com.wordsbyfarber.network.downloadAndParseJson
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

    Column {
        if (language.isEmpty()) {
            LanguageSelectionScreen { selectedLanguage ->
                language = selectedLanguage
                saveLanguage(context, language)
                isLoading = true

                coroutineScope.launch {
                    val jsonData = downloadAndParseJson(language)
                    val wordList = jsonData.map { Words(it.key, it.value) }
                    val database = Room.databaseBuilder(
                        context,
                        WordDatabase::class.java, "${language}_database"
                    ).build()
                    database.wordsDao().deleteAll()
                    database.wordsDao().insertAll(wordList)
                    isLoading = false
                    words = wordList
                }
            }
        } else if (isLoading) {
            CircularProgressIndicator()
        } else {
            // Display the 4 entries and the list of words based on the selection
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

@Preview
@Composable
fun PreviewMyApp() {
    WordsByFarberTheme {
        LanguageSelectionScreen {}
    }
}
