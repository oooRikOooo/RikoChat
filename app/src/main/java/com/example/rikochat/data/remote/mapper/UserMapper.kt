package com.example.rikochat.data.remote.mapper

import com.example.rikochat.data.remote.model.userResponse.UserDto
import com.example.rikochat.domain.model.user.User

class UserMapper {
    fun mapFromEntity(entity: UserDto): User {
        return User(
            username = entity.username,
            email = entity.email
        )
    }

    fun mapToEntity(user: User): UserDto {
        return UserDto(
            username = user.username,
            email = user.email
        )
    }
}