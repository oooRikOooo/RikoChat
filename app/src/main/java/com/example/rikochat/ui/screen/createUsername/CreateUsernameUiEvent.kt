package com.example.rikochat.ui.screen.createUsername

sealed class CreateUsernameUiEvent {
    object CreateUserName : CreateUsernameUiEvent()

    object CreateUsernameScreenIsClosed : CreateUsernameUiEvent()

    data class ShowSnackBar(val errorText: String) : CreateUsernameUiEvent()

    data class OnUsernameTextChanged(val text: String) : CreateUsernameUiEvent()

    object HideSnackBar : CreateUsernameUiEvent()
}