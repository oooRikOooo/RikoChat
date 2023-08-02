package com.example.rikochat.ui.screen.registration

sealed class RegistrationUiEvent {

    data class OnEmailTextChanged(val text: String) : RegistrationUiEvent()

    data class OnPasswordTextChanged(val text: String) : RegistrationUiEvent()

    data class OnUserNameTextChanged(val text: String) : RegistrationUiEvent()

    data class ShowSnackBar(val errorText: String) : RegistrationUiEvent()

    object HideSnackBar : RegistrationUiEvent()

    object Register : RegistrationUiEvent()
}