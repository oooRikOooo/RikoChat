package com.example.rikochat.ui.screen.main

import com.example.rikochat.domain.model.user.User

sealed class MainUserUiState {
    object Idle : MainUserUiState()

    object Loading : MainUserUiState()

    data class Error(val message: String) : MainUserUiState()

    object UserNotFound : MainUserUiState()

    data class SuccessfulLoad(val user: User) : MainUserUiState()
}