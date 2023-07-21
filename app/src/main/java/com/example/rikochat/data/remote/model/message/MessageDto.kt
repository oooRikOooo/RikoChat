package com.example.rikochat.data.remote.model.message

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val text: String,
    val timestamp: Long,
    val username: String,
    val id: String
)