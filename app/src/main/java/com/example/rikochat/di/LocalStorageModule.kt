package com.example.rikochat.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.rikochat.data.local.TokenRepositoryImpl
import com.example.rikochat.domain.repository.TokenRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chat_storage")

val localStorageModule = module {
    factory<TokenRepository> { TokenRepositoryImpl(androidContext().dataStore) }
}