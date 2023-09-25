package com.example.rikochat.ui.screen.main

import android.util.Log
import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.domain.model.user.User

data class MainViewModelState(
    val currentUser: User? = null,
    val rooms: List<ChatRoom> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
) {

    fun toUiState(): MainUiState {
        Log.d("riko", "toUiState: if (isLoading) return MainUiState.Loading")
        if (isLoading) return MainUiState.Loading

        Log.d("riko", "toUiState: if (error != null) return MainUiState.Error(error)")

        if (error != null) return MainUiState.Error(error)

        Log.d("riko", "toUiState: if (currentUser == null) return MainUiState.UserNotLoggedIn")
        if (currentUser == null) return MainUiState.UserNotLoggedIn

        return if (rooms.isEmpty())
            MainUiState.EmptyScreen(currentUser = currentUser)
        else
            MainUiState.SuccessLoad(currentUser = currentUser, rooms = rooms)
    }
}