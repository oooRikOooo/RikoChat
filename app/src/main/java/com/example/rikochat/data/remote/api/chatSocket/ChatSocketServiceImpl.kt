package com.example.rikochat.data.remote.api.chatSocket

import com.example.rikochat.data.remote.mapper.MessageMapper
import com.example.rikochat.data.remote.model.message.MessageDto
import com.example.rikochat.domain.api.chatSocket.ChatSocketService
import com.example.rikochat.domain.model.message.Message
import com.example.rikochat.utils.DataState
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class ChatSocketServiceImpl(
    private val client: HttpClient,
    private val messageMapper: MessageMapper
) : ChatSocketService {

    private var socket: WebSocketSession? = null

    override suspend fun initSession(username: String): DataState<Unit> {
        return try {
            socket = client.webSocketSession {
                url("${ChatSocketService.Endpoints.ChatSocket.url}?username=$username")
            }
            if (socket?.isActive == true)
                DataState.Success(Unit)
            else
                DataState.Error("Couldn't establish connection")
        } catch (e: Exception) {
            e.printStackTrace()
            DataState.Error(e.localizedMessage ?: "Unexpected error")
        }
    }

    override suspend fun sendMessage(message: String) {
        try {
            socket?.send(Frame.Text(message))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun observeMessages(): Flow<Message> {
        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    val messageDto = Json.decodeFromString<MessageDto>(json)
                    messageMapper.mapFromEntity(messageDto)
                } ?: flow { }
        } catch (e: Exception) {
            e.printStackTrace()
            flow { }
        }
    }

    override suspend fun closeSession() {
        socket?.close()
    }

}