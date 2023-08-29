package com.example.rikochat.data.remote.model.rest.message

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val id: String,
    val text: String,
    val timestamp: Long,
    val username: String,
    val roomId: String,
    val isRead: Boolean,
    val isLiked: Boolean
)