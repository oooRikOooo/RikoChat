package com.example.rikochat.domain.usecase.getCurrentUser

import com.example.rikochat.domain.model.user.User
import com.example.rikochat.domain.repository.CurrentUserRepository
import com.example.rikochat.utils.DataState

class GetCurrentUserUseCase(
    private val currentUserRepository: CurrentUserRepository
) {
    suspend operator fun invoke(): DataState<User> = currentUserRepository.getCurrentUser()
}