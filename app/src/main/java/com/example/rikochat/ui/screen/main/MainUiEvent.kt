package com.example.rikochat.ui.screen.main

sealed class MainUiEvent {

    data class OnDialogTitleTextChanged(val text: String) : MainUiEvent()

    data class ShowDialogTitleError(val text: String) : MainUiEvent()

    object CreateChatRoom : MainUiEvent()

    object LoadUserChatRooms : MainUiEvent()

    object LoadUser : MainUiEvent()

    object SignOut : MainUiEvent()
}