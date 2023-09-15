package com.example.rikochat.ui.screen.chat

import com.example.rikochat.domain.model.message.Message

sealed class ChatUiEvent {
    data class OnMessageChanged(val message: String) : ChatUiEvent()

    data class SendMessage(val roomId: String) : ChatUiEvent()

    data class ShowMessageActionsDialog(val message: Message) : ChatUiEvent()
    object HideMessageActionsDialog : ChatUiEvent()

    data class LikeMessage(val messageId: String) : ChatUiEvent()

    data class LoadRoomInfo(val roomId: String) : ChatUiEvent()

    data class AddUserToGroupChat(val roomId: String) : ChatUiEvent()

    data class OnAddUserToGroupChatRoomTextChanged(val text: String) : ChatUiEvent()
}