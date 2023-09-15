package com.example.rikochat.di

import androidx.room.Room
import com.example.rikochat.data.local.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "app_database").build()
    }
}