package com.example.rikochat.di

import com.example.rikochat.domain.usecase.addUserToGroupChat.AddUserToGroupChatUseCase
import com.example.rikochat.domain.usecase.createAccount.RegisterUseCase
import com.example.rikochat.domain.usecase.createChatRoom.CreateChatRoomUseCase
import com.example.rikochat.domain.usecase.getChatRoom.GetChatRoomUseCase
import com.example.rikochat.domain.usecase.getChatRoomMembers.GetChatRoomMembersUseCase
import com.example.rikochat.domain.usecase.getUser.GetUserUseCase
import com.example.rikochat.domain.usecase.getUserMessages.GetUserChatRoomsUseCase
import com.example.rikochat.domain.usecase.loginUser.LoginUserUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { RegisterUseCase(get()) }
    factory { LoginUserUseCase(get()) }
    factory { GetUserUseCase(get()) }
    factory { GetUserChatRoomsUseCase(get()) }
    factory { CreateChatRoomUseCase(get()) }
    factory { AddUserToGroupChatUseCase(get()) }
    factory { GetChatRoomUseCase(get()) }
    factory { GetChatRoomMembersUseCase(get()) }
}