package com.example.rikochat.ui.screen.selectUser

sealed class SelectUserUiState {

    object Idle : SelectUserUiState()

    data class SuccessLogin(val username: String) : SelectUserUiState()

    data class FailureLogin(val error: String) : SelectUserUiState()
}