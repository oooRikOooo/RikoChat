package com.example.rikochat.domain.api.auth

import com.example.rikochat.BuildConfig.BASE_URL
import com.example.rikochat.domain.model.isUserNameAvailable.IsUserNameAvailable
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.utils.DataState
import com.google.firebase.auth.FirebaseUser

interface AuthService {

    suspend fun createAccount(
        username: String,
        email: String,
        password: String
    ): DataState<User>

    suspend fun login(email: String, password: String): DataState<FirebaseUser>

    suspend fun checkIsUserNameAvailable(username: String): DataState<IsUserNameAvailable>

    sealed class Endpoints(val url: String) {
        object CheckIsUserNameAvailable : Endpoints("${BASE_URL}/isUserNameAvailable")
    }
}