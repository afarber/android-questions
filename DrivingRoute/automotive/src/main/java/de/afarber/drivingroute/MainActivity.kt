package de.afarber.drivingroute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import de.afarber.drivingroute.ui.MainScreen
import de.afarber.drivingroute.ui.theme.DrivingRouteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrivingRouteTheme {
                MainScreen()
            }
        }
    }
}