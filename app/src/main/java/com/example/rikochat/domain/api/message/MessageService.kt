package com.example.rikochat.domain.api.message

import com.example.rikochat.BuildConfig.BASE_URL
import com.example.rikochat.domain.model.message.Message
import com.example.rikochat.utils.DataState

interface MessageService {

    suspend fun getAllMessages(): DataState<List<Message>>

    sealed class Endpoints(val url: String){
        object GetAllMessages: Endpoints("$BASE_URL/messages")
    }
}