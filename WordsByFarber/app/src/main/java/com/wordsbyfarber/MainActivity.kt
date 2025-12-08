package com.wordsbyfarber

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.wordsbyfarber.ui.navigation.AppNavigation
import com.wordsbyfarber.ui.theme.WordsByFarberTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordsByFarberTheme {
                AppNavigation()
            }
        }
    }
}
