package com.example.rikochat.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.rikochat.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TokenRepositoryImpl(private val dataStore: DataStore<Preferences>) : TokenRepository {
    override suspend fun updateAuthToken(token: String) {
        dataStore.edit {
            it[TokenRepository.TokenPreferencesKeys.TOKEN] = token
        }
    }

    override suspend fun getAuthToken(): String {
        return dataStore.data.first()[TokenRepository.TokenPreferencesKeys.TOKEN] ?: ""
    }

}