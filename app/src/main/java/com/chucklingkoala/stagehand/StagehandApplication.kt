package com.chucklingkoala.stagehand

import android.app.Application
import com.chucklingkoala.stagehand.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class StagehandApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin for dependency injection
        startKoin {
            androidContext(this@StagehandApplication)
            modules(appModule)
        }
    }
}
