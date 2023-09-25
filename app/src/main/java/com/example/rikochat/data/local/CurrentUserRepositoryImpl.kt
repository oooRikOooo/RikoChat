package com.example.rikochat.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.domain.repository.CurrentUserRepository
import com.example.rikochat.utils.DataState
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CurrentUserRepositoryImpl(private val dataStore: DataStore<Preferences>): CurrentUserRepository {
    override suspend fun saveCurrentUser(user: User) {
        dataStore.edit {
            val userString = Json.encodeToString(user)

            it[CurrentUserRepository.UserPreferencesKeys.USER] = userString
        }
    }

    override suspend fun getCurrentUser(): DataState<User> {
        val userString = dataStore.data.first()[CurrentUserRepository.UserPreferencesKeys.USER]

        return userString?.let {
            if (it.isEmpty()) DataState.Error("User not found")
            else {
                val user = Json.decodeFromString<User>(it)
                DataState.Success(user)
            }
        } ?: kotlin.run {
            DataState.Error("User not found")
        }
    }
}