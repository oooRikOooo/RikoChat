package com.example.rikochat.ui.screen.chat

sealed class ChatUiEvent {
    data class OnMessageChanged(val message: String) : ChatUiEvent()

    data class SendMessage(val roomId: String) : ChatUiEvent()

    data class LoadRoomInfo(val roomId: String) : ChatUiEvent()

    data class AddUserToGroupChat(val roomId: String) : ChatUiEvent()

    data class OnAddUserToGroupChatRoomTextChanged(val text: String) : ChatUiEvent()
}