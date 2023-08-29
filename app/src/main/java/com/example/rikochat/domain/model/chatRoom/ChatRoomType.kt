package com.example.rikochat.domain.model.chatRoom

import kotlinx.serialization.Serializable

@Serializable
sealed class ChatRoomType(val name: String) {
    object Group : ChatRoomType("Group")
    object Direct : ChatRoomType("Direct")

    companion object {
        fun getChatRoomTypeByName(name: String): ChatRoomType {
            return if (name == "Group") Group else Direct
        }
    }
}
