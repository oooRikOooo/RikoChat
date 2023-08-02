package com.example.rikochat.domain.usecase.createAccount

import com.example.rikochat.domain.api.auth.AuthService
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.domain.usecase.checkIsUserNameAvailable.CheckIsUserNameAvailableUseCase
import com.example.rikochat.domain.usecase.saveUser.SaveUserUseCase
import com.example.rikochat.utils.DataState

class CreateAccountUseCase(
    private val authService: AuthService,
    private val checkIsUserNameAvailableUseCase: CheckIsUserNameAvailableUseCase,
    private val saveUserUseCase: SaveUserUseCase
) {
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String
    ): DataState<User> {
        return when (val result = checkIsUserNameAvailableUseCase.invoke(username)) {
            is DataState.Error -> {
                DataState.Error(result.message)
            }

            is DataState.Success -> {
                when (val resultCreateAccount =
                    authService.createAccount(username, email, password)) {
                    is DataState.Error -> {
                        DataState.Error(resultCreateAccount.message)
                    }

                    is DataState.Success -> {
                        saveUserUseCase.invoke(resultCreateAccount.data)
                        DataState.Success(resultCreateAccount.data)
                    }
                }
            }
        }

    }
}