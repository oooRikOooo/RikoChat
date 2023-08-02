package com.example.rikochat.di

import com.example.rikochat.ui.screen.chat.ChatViewModel
import com.example.rikochat.ui.screen.createUsername.CreateUsernameViewModel
import com.example.rikochat.ui.screen.login.LoginViewModel
import com.example.rikochat.ui.screen.registration.RegistrationViewModel
import com.example.rikochat.ui.screen.selectUserName.SelectUserNameViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SelectUserNameViewModel() }
    viewModel { ChatViewModel(get(), get()) }
    viewModel { RegistrationViewModel(get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { CreateUsernameViewModel(get(), get()) }
}