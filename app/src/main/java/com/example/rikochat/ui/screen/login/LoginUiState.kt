package com.example.rikochat.ui.screen.login

sealed class LoginUiState {
    object Idle : LoginUiState()

    object Loading : LoginUiState()

    object SuccessLogin : LoginUiState()

    data class FailedLogin(val error: String) : LoginUiState()
}