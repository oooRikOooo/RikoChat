package com.example.rikochat.ui.screen.main

import com.example.rikochat.domain.model.chatRoom.ChatRoom

sealed class MainUiState {
    object Idle : MainUiState()

    object Loading : MainUiState()

    data class Error(val message: String) : MainUiState()

    object EmptyScreen : MainUiState()

    data class SuccessLoad(val rooms: List<ChatRoom>) : MainUiState()
}