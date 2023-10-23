package com.example.rikochat.ui.screen.home

import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.domain.model.user.User

data class HomeViewModelState(
    val currentUser: User? = null,
    val rooms: List<ChatRoom> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
) {

    fun toUiState(): HomeUiState {
        if (isLoading) return HomeUiState.Loading

        if (error != null) return HomeUiState.Error(error)

        if (currentUser == null) return HomeUiState.UserNotLoggedIn

        return if (rooms.isEmpty())
            HomeUiState.EmptyScreen(currentUser = currentUser)
        else
            HomeUiState.SuccessLoad(currentUser = currentUser, rooms = rooms)
    }
}