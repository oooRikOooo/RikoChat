package com.example.rikochat.domain.model.user

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val email: String
)