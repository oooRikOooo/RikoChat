package com.example.rikochat.ui.screen.main

import com.example.rikochat.domain.model.chatRoom.ChatRoom

data class MainViewModelState(
    val rooms: List<ChatRoom> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {

    fun toUiState(): MainUiState {
        if (error != null) return MainUiState.Error(error)

        if (isLoading) return MainUiState.Loading

        return if (rooms.isEmpty())
            MainUiState.EmptyScreen
        else
            MainUiState.SuccessLoad(rooms)
    }
}