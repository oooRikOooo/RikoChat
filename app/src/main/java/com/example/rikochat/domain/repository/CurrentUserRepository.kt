package com.example.rikochat.domain.repository

import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.utils.DataState

interface CurrentUserRepository {
    suspend fun saveCurrentUser(user: User)

    suspend fun getCurrentUser(): DataState<User>

    object UserPreferencesKeys {
        val USER = stringPreferencesKey("user")
    }
}