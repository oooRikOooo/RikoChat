package com.example.rikochat.ui.screen.chat

import com.example.rikochat.domain.model.message.Message

sealed class ChatUiState {

    object Idle: ChatUiState()

    object Loading: ChatUiState()

    data class SuccessLoad(val data: List<Message>): ChatUiState()

    object EmptyChat: ChatUiState()

    object FailureLoad: ChatUiState()
}