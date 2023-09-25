package com.example.rikochat.ui.screen.main

import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.domain.model.user.User

sealed class MainUiState {
    object Idle : MainUiState()

    object Loading : MainUiState()

    object UserNotLoggedIn : MainUiState()

    data class Error(val message: String) : MainUiState()

    data class EmptyScreen(val currentUser: User) : MainUiState()

    data class SuccessLoad(val currentUser: User, val rooms: List<ChatRoom>) : MainUiState()
}