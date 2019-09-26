package com.izyver.gati.presentation

import android.app.Application
import com.izyver.gati.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class GatiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@GatiApplication)
            modules(appModule)
        }
    }
}