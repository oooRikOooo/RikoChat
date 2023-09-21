package com.example.rikochat.domain.api.auth

import com.example.rikochat.BuildConfig.BASE_URL
import com.example.rikochat.utils.DataState
import com.google.firebase.auth.FirebaseUser

interface AuthService {

    suspend fun register(
        username: String,
        email: String,
        password: String
    ): DataState<Unit>

    suspend fun login(email: String, password: String): DataState<FirebaseUser>

    sealed class Endpoints(val url: String) {
        object Login : Endpoints("$BASE_URL/login")
        object Register : Endpoints("$BASE_URL/register")
    }
}