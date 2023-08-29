package com.example.rikochat.data.remote.model.rest.chatRoom

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateChatRoomRequestBody(
    @SerialName("title")
    val title: String,
    @SerialName("type")
    val type: String,
    @SerialName("username")
    val ownerUsername: String
)
