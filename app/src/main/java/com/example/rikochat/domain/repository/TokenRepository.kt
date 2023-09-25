package com.example.rikochat.domain.repository

import androidx.datastore.preferences.core.stringPreferencesKey

interface TokenRepository {

    suspend fun updateAuthToken(token: String)

    suspend fun getAuthToken(): String

    suspend fun observeAuthToken(): kotlinx.coroutines.flow.Flow<String>


    object TokenPreferencesKeys {
        val TOKEN = stringPreferencesKey("token")
    }

}