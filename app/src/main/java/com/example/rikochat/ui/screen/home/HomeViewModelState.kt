package com.example.rikochat.ui.screen.home

import android.util.Log
import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.domain.model.user.User

data class HomeViewModelState(
    val currentUser: User? = null,
    val rooms: List<ChatRoom> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
) {

    fun toUiState(): HomeUiState {
        Log.d("riko", "toUiState: if (isLoading) return MainUiState.Loading")
        if (isLoading) return HomeUiState.Loading

        Log.d("riko", "toUiState: if (error != null) return MainUiState.Error(error)")

        if (error != null) return HomeUiState.Error(error)

        Log.d("riko", "toUiState: if (currentUser == null) return MainUiState.UserNotLoggedIn")
        if (currentUser == null) return HomeUiState.UserNotLoggedIn

        return if (rooms.isEmpty())
            HomeUiState.EmptyScreen(currentUser = currentUser)
        else
            HomeUiState.SuccessLoad(currentUser = currentUser, rooms = rooms)
    }
}