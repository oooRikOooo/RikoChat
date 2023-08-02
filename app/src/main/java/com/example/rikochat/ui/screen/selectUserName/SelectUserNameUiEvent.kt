package com.example.rikochat.ui.screen.selectUserName

sealed class SelectUserNameUiEvent {

    data class OnUsernameChanged(val username: String) : SelectUserNameUiEvent()

    object OnJoinClick : SelectUserNameUiEvent()

    object ScreenClosed : SelectUserNameUiEvent()
}