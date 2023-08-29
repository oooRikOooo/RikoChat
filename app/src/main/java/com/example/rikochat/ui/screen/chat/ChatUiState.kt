package com.example.rikochat.ui.screen.chat

import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.domain.model.message.Message
import com.example.rikochat.domain.model.user.User

sealed class ChatUiState {

    object Idle : ChatUiState()

    object Loading : ChatUiState()

    data class SuccessLoad(
        val data: List<Message>,
        val chatRoom: ChatRoom,
        val chatRoomMembers: List<User>
    ) : ChatUiState()

    data class EmptyChat(
        val chatRoom: ChatRoom,
        val chatRoomMembers: List<User>
    ) : ChatUiState()

    object FailureLoad : ChatUiState()
}