package com.example.rikochat.data.remote.model.rest.chatRoom

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRoomDto(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String,
    @SerialName("picture")
    val picture: String,
    @SerialName("type")
    val type: String,
    @SerialName("lastMessageTimeStamp")
    val lastMessageTimeStamp: Long,
    @SerialName("lastMessage")
    val lastMessage: String,
    @SerialName("ownerId")
    val ownerId: String
)