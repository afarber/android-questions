package com.wordsbyfarber

// Main activity that hosts the Compose UI and manages app navigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.wordsbyfarber.data.repository.DictionaryRepository
import com.wordsbyfarber.data.repository.PreferencesRepository
import com.wordsbyfarber.ui.navigation.AppNavigation
import com.wordsbyfarber.ui.theme.WordsByFarberTheme

class MainActivity : ComponentActivity() {
    
    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var dictionaryRepository: DictionaryRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize repositories
        val sharedPreferences = getSharedPreferences("words_by_farber", MODE_PRIVATE)
        preferencesRepository = PreferencesRepository(sharedPreferences)
        
        val okHttpClient = okhttp3.OkHttpClient()
        val downloader = com.wordsbyfarber.data.network.DictionaryDownloader(okHttpClient)
        val parser = com.wordsbyfarber.data.network.DictionaryParser()
        dictionaryRepository = com.wordsbyfarber.data.repository.DictionaryRepository(this, downloader, parser)
        
        setContent {
            WordsByFarberTheme {
                val navController = rememberNavController()
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        preferencesRepository = preferencesRepository,
                        dictionaryRepository = dictionaryRepository
                    )
                }
            }
        }
    }
}