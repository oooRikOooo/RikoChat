package com.example.rikochat.ui.screen.selectUserName

sealed class SelectUserNameUiState {

    object Idle : SelectUserNameUiState()

    data class SuccessLogin(val username: String) : SelectUserNameUiState()

    data class FailureLogin(val error: String) : SelectUserNameUiState()
}