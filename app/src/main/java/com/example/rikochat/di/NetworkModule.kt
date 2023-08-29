package com.example.rikochat.di

import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.rikochat.data.remote.api.auth.AuthServiceImpl
import com.example.rikochat.data.remote.api.chatSocket.ChatSocketServiceImpl
import com.example.rikochat.data.remote.api.message.RoomServiceImpl
import com.example.rikochat.data.remote.api.user.UserServiceImpl
import com.example.rikochat.domain.api.auth.AuthService
import com.example.rikochat.domain.api.chatSocket.ChatSocketService
import com.example.rikochat.domain.api.message.RoomService
import com.example.rikochat.domain.api.user.UserService
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient>(named("RestHttpClient")) {
        HttpClient(OkHttp) {
            engine {
                addInterceptor(get<ChuckerInterceptor>())
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.BODY
            }
            install(ContentNegotiation) {
                json()
            }
        }
    }

    single<HttpClient>(named("WebSocketsHttpClient")) {
        HttpClient(CIO) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.BODY
            }
            install(WebSockets)
        }
    }

    single<ChuckerInterceptor> {
        ChuckerInterceptor.Builder(androidContext())
            .collector(ChuckerCollector(androidContext()))
            .maxContentLength(250000L)
            .redactHeaders(emptySet())
            .alwaysReadResponseBody(false)
            .build()
    }

    single<RoomService> { RoomServiceImpl(get(named("RestHttpClient")), get(), get(), get()) }

    single<ChatSocketService> {
        ChatSocketServiceImpl(
            get(named("WebSocketsHttpClient")),
            get(),
            get(),
            get()
        )
    }

    single<AuthService> { AuthServiceImpl(get(named("RestHttpClient")), get()) }

    single<UserService> { UserServiceImpl(get(named("RestHttpClient")), get(), get()) }
}