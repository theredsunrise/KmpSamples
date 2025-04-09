package org.example.kmpsamples.presentation

import android.app.Application
import org.example.kmpsamples.shared.androidModules
import org.example.kmpsamples.shared.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@CustomApplication)
        }
        loadKoinModules(androidModules(this))
    }
}