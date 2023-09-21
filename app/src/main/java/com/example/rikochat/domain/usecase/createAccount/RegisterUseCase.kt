package com.example.rikochat.domain.usecase.createAccount

import com.example.rikochat.domain.api.auth.AuthService
import com.example.rikochat.utils.DataState

class RegisterUseCase(
    private val authService: AuthService
) {
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String
    ): DataState<Unit> {
        return authService.register(username, email, password)
    }
}