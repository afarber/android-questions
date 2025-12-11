/**
 * Main Activity - the single entry point for the app's UI.
 *
 * This app uses the single-Activity architecture with Jetpack Compose.
 * All screens are Composable functions managed by Navigation 3, not separate Activities.
 */
package com.wordsbyfarber

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.wordsbyfarber.ui.navigation.AppNavigation
import com.wordsbyfarber.ui.theme.WordsByFarberTheme

/**
 * The app's single Activity that hosts all Compose UI.
 *
 * ComponentActivity is the base class for Compose-based activities.
 * It provides setContent {} for declaring Compose UI.
 */
class MainActivity : ComponentActivity() {

    /**
     * Called when the Activity is first created.
     *
     * @param savedInstanceState Bundle containing saved state from previous instance,
     *                           or null if this is a fresh start
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display (content extends behind system bars)
        enableEdgeToEdge()

        // setContent {} is the Compose entry point
        // Everything inside is Composable UI code
        setContent {
            // Apply the app's Material 3 theme
            WordsByFarberTheme {
                // Root navigation component that manages all screens
                AppNavigation()
            }
        }
    }
}
