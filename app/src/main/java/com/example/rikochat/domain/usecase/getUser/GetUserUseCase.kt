package com.example.rikochat.domain.usecase.getUser

import com.example.rikochat.domain.api.user.UserService
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.utils.DataState

class GetUserUseCase(
    private val userService: UserService
) {
    suspend operator fun invoke(username: String): DataState<User> {
        return userService.getUser(username)
    }
}