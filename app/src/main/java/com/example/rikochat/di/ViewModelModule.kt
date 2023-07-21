package com.example.rikochat.di

import com.example.rikochat.ui.screen.chat.ChatViewModel
import com.example.rikochat.ui.screen.selectUser.SelectUserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SelectUserViewModel() }
    viewModel { ChatViewModel(get(), get()) }
}