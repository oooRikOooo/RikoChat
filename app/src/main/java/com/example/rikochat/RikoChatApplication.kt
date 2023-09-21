package com.example.rikochat

import android.app.Application
import com.example.rikochat.di.localStorageModule
import com.example.rikochat.di.mapperModule
import com.example.rikochat.di.networkModule
import com.example.rikochat.di.useCaseModule
import com.example.rikochat.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RikoChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@RikoChatApplication)
            modules(
                localStorageModule,
                networkModule,
                useCaseModule,
                mapperModule,
                viewModelModule
            )
        }
    }
}