package com.example.rikochat.ui.screen.home

sealed class HomeUiEvent {

    data class OnDialogTitleTextChanged(val text: String) : HomeUiEvent()

    data class ShowDialogTitleError(val text: String) : HomeUiEvent()

    object CreateChatRoom : HomeUiEvent()

    object LoadInitialData : HomeUiEvent()

    object SignOut : HomeUiEvent()
}