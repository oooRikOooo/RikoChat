package com.example.rikochat.domain.api.chatSocket

import com.example.rikochat.BuildConfig.BASE_URL
import com.example.rikochat.domain.model.message.Message
import com.example.rikochat.utils.DataState
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {

    suspend fun initSession(username: String): DataState<Unit>

    suspend fun sendMessage(message: String)

    fun observeMessages(): Flow<Message>

    suspend fun closeSession()

    sealed class Endpoints(val url: String){
        object ChatSocket: Endpoints("$BASE_URL/chat-socket")
    }
}