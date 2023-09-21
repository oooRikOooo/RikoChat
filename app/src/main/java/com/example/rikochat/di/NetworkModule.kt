package com.example.rikochat.di

import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.rikochat.data.local.TokenRepositoryImpl
import com.example.rikochat.data.remote.api.auth.AuthServiceImpl
import com.example.rikochat.data.remote.api.chatSocket.WebSocketManager
import com.example.rikochat.data.remote.api.message.RoomServiceImpl
import com.example.rikochat.data.remote.api.user.UserServiceImpl
import com.example.rikochat.domain.api.auth.AuthService
import com.example.rikochat.domain.api.message.RoomService
import com.example.rikochat.domain.api.user.UserService
import com.example.rikochat.domain.repository.TokenRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
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

            defaultRequest {
                    val repository = get<TokenRepository>()
                    val token = runBlocking { repository.getAuthToken() }

                    if (token.isNotEmpty()){
                        header("Authorization", "Bearer $token")
                    }


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

    single {
        WebSocketManager(
            get(named("WebSocketsHttpClient")),
            get(),
            get(),
            get()
        )
    }

    single<AuthService> { AuthServiceImpl(get(named("RestHttpClient")), get(), get()) }

    single<UserService> { UserServiceImpl(get(named("RestHttpClient")), get(), get()) }
}