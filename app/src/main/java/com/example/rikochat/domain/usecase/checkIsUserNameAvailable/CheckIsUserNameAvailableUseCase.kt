package com.example.rikochat.domain.usecase.checkIsUserNameAvailable

import com.example.rikochat.domain.api.auth.AuthService
import com.example.rikochat.domain.model.isUserNameAvailable.IsUserNameAvailable
import com.example.rikochat.utils.DataState

class CheckIsUserNameAvailableUseCase(
    private val authService: AuthService
) {
    suspend operator fun invoke(username: String): DataState<IsUserNameAvailable> {
        return authService.checkIsUserNameAvailable(username)
    }
}