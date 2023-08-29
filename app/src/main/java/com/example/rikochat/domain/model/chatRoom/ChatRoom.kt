package com.example.rikochat.domain.model.chatRoom

import kotlinx.serialization.Serializable

@Serializable
data class ChatRoom(
    val id: String,
    val title: String,
    val picture: String,
    val type: ChatRoomType,
    val lastMessageTimeStampFormatted: String,
    var lastMessage: String,
    val ownerId: String
)
