package com.example.rikochat.ui.screen.selectUser

sealed class SelectUserUiEvent {

    data class OnUsernameChanged(val username: String): SelectUserUiEvent()

    object OnJoinClick: SelectUserUiEvent()
}