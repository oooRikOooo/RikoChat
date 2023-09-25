package com.example.rikochat.ui.screen.home

import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.domain.model.user.User

sealed class HomeUiState {
    object Idle : HomeUiState()

    object Loading : HomeUiState()

    object UserNotLoggedIn : HomeUiState()

    data class Error(val message: String) : HomeUiState()

    data class EmptyScreen(val currentUser: User) : HomeUiState()

    data class SuccessLoad(val currentUser: User, val rooms: List<ChatRoom>) : HomeUiState()
}