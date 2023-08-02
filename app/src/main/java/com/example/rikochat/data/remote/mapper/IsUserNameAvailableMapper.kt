package com.example.rikochat.data.remote.mapper

import com.example.rikochat.data.remote.model.isUserNameAvailableResponse.IsUserNameAvailableDto
import com.example.rikochat.domain.model.isUserNameAvailable.IsUserNameAvailable

class IsUserNameAvailableMapper {
    fun mapFromEntity(entity: IsUserNameAvailableDto): IsUserNameAvailable {
        return IsUserNameAvailable(
            username = entity.username,
            isUserNameAvailable = entity.isUserNameAvailable
        )
    }
}