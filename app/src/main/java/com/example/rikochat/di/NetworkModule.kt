package com.example.rikochat.di

import com.example.rikochat.data.remote.api.chatSocket.ChatSocketServiceImpl
import com.example.rikochat.data.remote.api.message.MessageServiceImpl
import com.example.rikochat.domain.api.chatSocket.ChatSocketService
import com.example.rikochat.domain.api.message.MessageService
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.WebSockets
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> {
        HttpClient(CIO) {
            install(Logging)
            install(WebSockets)
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    }

    single<MessageService> { MessageServiceImpl(get(), get()) }

    single<ChatSocketService> { ChatSocketServiceImpl(get(), get()) }
}