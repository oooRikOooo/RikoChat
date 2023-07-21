package com.example.rikochat.ui.screen.chat

sealed class ChatUiEvent {
    data class OnMessageChanged(val message: String): ChatUiEvent()

    object SendMessage: ChatUiEvent()
}