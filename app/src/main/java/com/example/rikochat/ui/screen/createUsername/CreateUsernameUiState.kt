package com.example.rikochat.ui.screen.createUsername

sealed class CreateUsernameUiState {

    object Idle : CreateUsernameUiState()

    object Loading : CreateUsernameUiState()

    object SuccessfulCreate : CreateUsernameUiState()

    data class FailedCreate(val error: String) : CreateUsernameUiState()
}