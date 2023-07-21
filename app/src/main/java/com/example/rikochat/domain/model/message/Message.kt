package com.example.rikochat.domain.model.message

data class Message(
    val text: String,
    val formattedDate: String,
    val formattedTime: String,
    val username: String,
)
