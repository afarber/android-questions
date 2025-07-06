package com.wordsbyfarber

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WordsByFarberApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
    }
}