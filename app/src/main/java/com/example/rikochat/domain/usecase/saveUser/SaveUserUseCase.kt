package com.example.rikochat.domain.usecase.saveUser

import com.example.rikochat.domain.api.user.UserService
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.utils.DataState

class SaveUserUseCase(
    private val userService: UserService
) {
    suspend operator fun invoke(user: User): DataState<Unit> {
        return userService.saveUser(user)
    }
}