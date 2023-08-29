package com.example.rikochat.data.remote.api.chatSocket

import com.example.rikochat.data.remote.mapper.ChatRoomMapper
import com.example.rikochat.data.remote.mapper.MessageMapper
import com.example.rikochat.data.remote.mapper.UserMapper
import com.example.rikochat.data.remote.model.rest.message.MessageDto
import com.example.rikochat.data.remote.model.rest.user.UserDto
import com.example.rikochat.domain.api.chatSocket.ChatSocketService
import com.example.rikochat.domain.model.message.Message
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.utils.DataState
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ChatSocketServiceImpl(
    private val client: HttpClient,
    private val messageMapper: MessageMapper,
    private val chatRoomMapper: ChatRoomMapper,
    private val userMapper: UserMapper
) : ChatSocketService {

    private var socket: WebSocketSession? = null

    var sharedFlowMessages = MutableSharedFlow<Message>()

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

    override fun isWebSocketConnected(): Boolean {
        return socket != null
    }

    override suspend fun sendMessage(message: MessageDto) {
        try {
            val json = Json.encodeToString(message)
            val sendMessage = "send_message:$json"
            socket?.send(Frame.Text(sendMessage))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun observeMessages(): Flow<Message> {
        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter {
                    if (it is Frame.Text) {
                        val message = it.readText()

                        val parts = message.split(":", limit = 2)

                        if (parts[0] != "message_sent") return@filter false

                        return@filter true

                    } else return@filter false
                }?.map {
                    val mappedJson = (it as? Frame.Text)?.readText() ?: ""
                    val parts = mappedJson.split(":", limit = 2)
                    val messageDto = Json.decodeFromString<MessageDto>(parts[1])
                    messageMapper.mapFromEntity(messageDto)
                } ?: flow {}
        } catch (e: Exception) {
            e.printStackTrace()
            flow {}
        }
    }

//    override fun observeRooms(): Flow<List<ChatRoom>> {
//        return try {
//            socket?.incoming
//                ?.receiveAsFlow()
//                ?.filter {
//                    if (it is Frame.Text) {
//                        val message = it.readText()
//
//                        val parts = message.split(":")
//
//                        if (parts[0] != "user_added") return@filter false
//
//                        return@filter true
//
//                    } else return@filter false
//                }?.map {
//                    val mappedJson = (it as? Frame.Text)?.readText() ?: ""
//                    val parts = mappedJson.split(":")
//                    val roomsDto = Json.decodeFromString<List<ChatRoomDto>>(parts[1])
//                    chatRoomMapper.mapFromEntityList(roomsDto)
//                } ?: flow { }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            flow { }
//        }
//    }

//    override fun observeChatRoomsDetails(roomId: String): Flow<ChatRoom> {
//        return try {
//            socket?.incoming
//                ?.receiveAsFlow()
//                ?.retryWhen { cause, attempt ->
//                    cause is IllegalArgumentException
//                }
//                ?.filter {
//                    if (it is Frame.Text) {
//                        val message = it.readText()
//
//                        val parts = message.split(":")
//
//                        if (parts[0] != "user_added") return@filter false
//
//                        return@filter true
//
//                    } else return@filter false
//                }
//                ?.map {
//                    val mappedJson = (it as? Frame.Text)?.readText() ?: ""
//                    val roomDto = Json.decodeFromString<ChatRoomDto>(mappedJson)
//                    chatRoomMapper.mapFromEntity(roomDto)
//                }
//                ?.filter {
//                    it.id == roomId
//                } ?: flow { }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            flow { }
//        }
//    }

    override fun observeChatRoomMembers(): Flow<List<User>> {
        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter {
                    if (it is Frame.Text) {
                        val message = it.readText()

                        val parts = message.split(":", limit = 2)

                        if (parts[0] != "user_added" || parts[0] != "user_removed") return@filter false

                        return@filter true

                    } else return@filter false
                }?.map {
                    val mappedJson = (it as? Frame.Text)?.readText() ?: ""
                    val parts = mappedJson.split(":", limit = 2)
                    val usersDto = Json.decodeFromString<List<UserDto>>(parts[1])
                    userMapper.mapFromEntityList(usersDto)
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