/**
 * Custom Application class for app-wide initialization.
 *
 * This class is instantiated before any Activity and runs once per app process.
 * Used here to initialize Koin dependency injection framework.
 *
 * Must be registered in AndroidManifest.xml:
 * <application android:name=".WordsByFarberApplication" ...>
 */
package com.wordsbyfarber

import android.app.Application
import com.wordsbyfarber.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Application subclass that initializes Koin DI on app startup.
 *
 * Application = base class for maintaining global application state
 * This runs before MainActivity.onCreate()
 */
class WordsByFarberApplication : Application() {

    /**
     * Called when the application is starting, before any other application objects are created.
     */
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin dependency injection
        // startKoin {} configures Koin with a DSL (Domain Specific Language)
        startKoin {
            // Enable Koin logging for debugging
            androidLogger()

            // Provide Android context to Koin for dependencies that need it
            // this@WordsByFarberApplication = explicit reference to outer class
            // (needed because we're inside the startKoin lambda)
            androidContext(this@WordsByFarberApplication)

            // Load our dependency definitions from AppModule.kt
            modules(appModule)
        }
    }
}
