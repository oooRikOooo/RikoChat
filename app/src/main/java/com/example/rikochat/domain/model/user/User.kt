package com.example.rikochat.domain.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("uid")
    val uid: String,
    @SerialName("username")
    val username: String,
    @SerialName("email")
    val email: String
)