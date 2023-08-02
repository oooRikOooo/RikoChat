package com.example.rikochat.data.remote.model.userResponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("username")
    val username: String,
    @SerialName("email")
    val email: String
)