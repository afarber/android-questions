package com.wordsbyfarber

import android.app.Application
import com.wordsbyfarber.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class WordsByFarberApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@WordsByFarberApplication)
            modules(appModule)
        }
    }
}
