package com.example.rikochat.data.remote.model.rest.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("uid")
    val uid: String,
    @SerialName("username")
    val username: String,
    @SerialName("email")
    val email: String
)