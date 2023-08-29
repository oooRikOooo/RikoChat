package com.example.rikochat.ui.screen.chat

import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.domain.model.message.Message
import com.example.rikochat.domain.model.user.User

data class ChatViewModelState(
    val messages: List<Message>? = null,
    val chatRoom: ChatRoom? = null,
    val chatRoomMembers: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    fun toUiState(): ChatUiState {

        if (isLoading) return ChatUiState.Loading

        if (chatRoom == null || error != null) {
            return ChatUiState.FailureLoad
        }

        return if (messages.isNullOrEmpty())
            ChatUiState.EmptyChat(chatRoom, chatRoomMembers)
        else
            ChatUiState.SuccessLoad(messages, chatRoom, chatRoomMembers)
    }
}