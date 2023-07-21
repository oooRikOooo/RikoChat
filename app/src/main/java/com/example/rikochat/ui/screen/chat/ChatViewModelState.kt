package com.example.rikochat.ui.screen.chat

import com.example.rikochat.domain.model.message.Message

data class ChatViewModelState(
    val messages: List<Message>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    fun toUiState(): ChatUiState {
        if (error != null) return ChatUiState.FailureLoad

        if (isLoading) return ChatUiState.Loading

        return if (messages.isNullOrEmpty())
            ChatUiState.EmptyChat
        else
            ChatUiState.SuccessLoad(messages)
    }
}