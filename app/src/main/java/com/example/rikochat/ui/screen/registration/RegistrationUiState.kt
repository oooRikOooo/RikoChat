package com.example.rikochat.ui.screen.registration

sealed class RegistrationUiState {
    data class FailedRegistration(val error: String) : RegistrationUiState()

    object SuccessfulRegister : RegistrationUiState()

    object Idle : RegistrationUiState()

    object Loading : RegistrationUiState()
}