package com.example.rikochat.domain.api.message

import com.example.rikochat.domain.model.message.Message
import com.example.rikochat.utils.DataState

interface MessageService {

    suspend fun getAllMessages(): DataState<List<Message>>

    companion object {
        const val BASE_URL = "ws://192.168.88.162:8080"
    }

    sealed class Endpoints(val url: String){
        object GetAllMessages: Endpoints("$BASE_URL/messages")
    }
}