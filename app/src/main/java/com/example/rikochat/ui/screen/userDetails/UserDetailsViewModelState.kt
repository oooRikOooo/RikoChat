package com.example.rikochat.ui.screen.userDetails

import com.example.rikochat.data.remote.api.ApiErrors
import com.example.rikochat.domain.model.user.User

data class UserDetailsViewModelState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val error: String? = null
) {
    fun toUiState(): UserDetailsUiState {
        if (isLoading) return UserDetailsUiState.Loading

        if (error != null) return UserDetailsUiState.Error(error)

        return if (user != null)
            UserDetailsUiState.SuccessLoad(user)
        else UserDetailsUiState.Error(ApiErrors.UserNotFound.errorMessage)
    }
}