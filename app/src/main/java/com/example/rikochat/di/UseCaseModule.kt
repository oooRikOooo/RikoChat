package com.example.rikochat.di

import com.example.rikochat.domain.usecase.checkIsUserNameAvailable.CheckIsUserNameAvailableUseCase
import com.example.rikochat.domain.usecase.createAccount.CreateAccountUseCase
import com.example.rikochat.domain.usecase.getUser.GetUserUseCase
import com.example.rikochat.domain.usecase.loginUser.LoginUserUseCase
import com.example.rikochat.domain.usecase.saveUser.SaveUserUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { CreateAccountUseCase(get(), get(), get()) }
    factory { LoginUserUseCase(get()) }
    factory { CheckIsUserNameAvailableUseCase(get()) }
    factory { GetUserUseCase(get()) }
    factory { SaveUserUseCase(get()) }
}