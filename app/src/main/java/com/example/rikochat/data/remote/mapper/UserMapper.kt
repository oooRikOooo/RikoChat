package com.example.rikochat.data.remote.mapper

import com.example.rikochat.data.remote.model.rest.user.UserDto
import com.example.rikochat.domain.model.user.User

class UserMapper : BaseMapper<User, UserDto> {
    override fun mapFromEntity(dto: UserDto): User {

        return User(
            username = dto.username,
            email = dto.email
        )
    }

    override fun mapToEntity(model: User): UserDto {
        return UserDto(
            username = model.username,
            email = model.email
        )
    }

    fun mapFromEntityList(entityList: List<UserDto>): List<User> {
        return entityList.map { mapFromEntity(it) }
    }

    fun mapToEntityList(userList: List<User>): List<UserDto> {
        return userList.map { mapToEntity(it) }
    }
}