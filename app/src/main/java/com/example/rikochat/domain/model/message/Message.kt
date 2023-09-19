package com.example.rikochat.domain.model.message

data class Message(
    val id: String,
    val text: String,
    val formattedDate: String,
    val formattedTime: String,
    val timestamp: Long,
    val username: String,
    val roomId: String,
    val isRead: Boolean,
    val usernamesWhoLiked: MutableList<String>
)
