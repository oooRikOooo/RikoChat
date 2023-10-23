package com.example.rikochat

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.example.rikochat.di.mapperModule
import com.example.rikochat.di.networkModule
import com.example.rikochat.di.repositoryModule
import com.example.rikochat.di.useCaseModule
import com.example.rikochat.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RikoChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(p0: Activity, p1: Bundle?) {
                Log.d("riko", "onActivityCreated")
            }

            override fun onActivityStarted(p0: Activity) {
                Log.d("riko", "onActivityStarted")
            }

            override fun onActivityResumed(p0: Activity) {
                Log.d("riko", "onActivityResumed")
            }

            override fun onActivityPaused(p0: Activity) {
                Log.d("riko", "onActivityPaused")
            }

            override fun onActivityStopped(p0: Activity) {
                Log.d("riko", "onActivityStopped")
            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
                Log.d("riko", "onActivitySaveInstanceState")
            }

            override fun onActivityDestroyed(p0: Activity) {
                Log.d("riko", "onActivityDestroyed")
            }

        })
        startKoin {
            androidLogger()
            androidContext(this@RikoChatApplication)
            modules(
                repositoryModule,
                networkModule,
                useCaseModule,
                mapperModule,
                viewModelModule
            )
        }
    }
}