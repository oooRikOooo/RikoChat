package com.example.rikochat.ui.screen.login

sealed class LoginUiEvent {
    data class OnEmailTextChanged(val text: String) : LoginUiEvent()

    data class OnPasswordTextChanged(val text: String) : LoginUiEvent()

    object Login : LoginUiEvent()

    object LoginScreenIsClosed : LoginUiEvent()

    data class ShowSnackBar(val errorText: String) : LoginUiEvent()

    object HideSnackBar : LoginUiEvent()
}