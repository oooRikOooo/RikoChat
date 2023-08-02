package com.example.rikochat.data.remote.api.message

import com.example.rikochat.data.remote.mapper.MessageMapper
import com.example.rikochat.data.remote.model.message.MessageDto
import com.example.rikochat.domain.api.message.MessageService
import com.example.rikochat.domain.model.message.Message
import com.example.rikochat.utils.DataState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class MessageServiceImpl(
    private val client: HttpClient,
    private val messageMapper: MessageMapper
) : MessageService {
    override suspend fun getAllMessages(): DataState<List<Message>> {
        return try {
            val messages =
                client.get(MessageService.Endpoints.GetAllMessages.url).body<List<MessageDto>>()
            return DataState.Success(messageMapper.mapFromEntityList(messages))
        } catch (e: Exception) {
            DataState.Error(e.message ?: "")
        }
    }
}