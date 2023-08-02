package com.example.rikochat.domain.usecase.loginUser

import com.example.rikochat.domain.api.auth.AuthService
import com.example.rikochat.utils.DataState
import com.google.firebase.auth.FirebaseUser

class LoginUserUseCase(
    private val authService: AuthService
) {
    suspend operator fun invoke(email: String, password: String): DataState<FirebaseUser> {
        return authService.login(email, password)
    }
}