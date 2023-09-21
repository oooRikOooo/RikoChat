package com.example.rikochat.domain.repository

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow

interface TokenRepository {

    suspend fun saveAuthToken(token: String)

    suspend fun getAuthToken(): String

    object TokenPreferencesKeys {
        val TOKEN = stringPreferencesKey("token")
    }

}