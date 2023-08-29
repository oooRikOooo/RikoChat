package com.example.rikochat.di

import com.example.rikochat.data.remote.mapper.ChatRoomMapper
import com.example.rikochat.data.remote.mapper.IsUserNameAvailableMapper
import com.example.rikochat.data.remote.mapper.MessageMapper
import com.example.rikochat.data.remote.mapper.UserMapper
import org.koin.dsl.module

val mapperModule = module {
    single { MessageMapper() }
    single { IsUserNameAvailableMapper() }
    single { UserMapper() }
    single { ChatRoomMapper() }
}