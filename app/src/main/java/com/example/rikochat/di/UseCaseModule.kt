package com.example.rikochat.di

import com.example.rikochat.domain.usecase.addUserToGroupChat.AddUserToGroupChatUseCase
import com.example.rikochat.domain.usecase.checkIsUserNameAvailable.CheckIsUserNameAvailableUseCase
import com.example.rikochat.domain.usecase.createAccount.CreateAccountUseCase
import com.example.rikochat.domain.usecase.createChatRoom.CreateChatRoomUseCase
import com.example.rikochat.domain.usecase.getChatRoom.GetChatRoomUseCase
import com.example.rikochat.domain.usecase.getChatRoomMembers.GetChatRoomMembersUseCase
import com.example.rikochat.domain.usecase.getUser.GetUserUseCase
import com.example.rikochat.domain.usecase.getUserMessages.GetUserChatRoomsUseCase
import com.example.rikochat.domain.usecase.loginUser.LoginUserUseCase
import com.example.rikochat.domain.usecase.saveUser.SaveUserUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { CreateAccountUseCase(get(), get(), get()) }
    factory { LoginUserUseCase(get()) }
    factory { CheckIsUserNameAvailableUseCase(get()) }
    factory { GetUserUseCase(get()) }
    factory { SaveUserUseCase(get()) }
    factory { GetUserChatRoomsUseCase(get()) }
    factory { CreateChatRoomUseCase(get()) }
    factory { AddUserToGroupChatUseCase(get()) }
    factory { GetChatRoomUseCase(get()) }
    factory { GetChatRoomMembersUseCase(get()) }
}