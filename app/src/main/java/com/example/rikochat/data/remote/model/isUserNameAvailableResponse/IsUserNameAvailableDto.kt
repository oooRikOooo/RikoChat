package com.example.rikochat.data.remote.model.isUserNameAvailableResponse

import kotlinx.serialization.Serializable

@Serializable
data class IsUserNameAvailableDto(
    val username: String,
    val isUserNameAvailable: Boolean
)