package com.example.rikochat.data.remote.api.chatSocket

import android.util.Log
import com.example.rikochat.data.remote.mapper.ChatRoomMapper
import com.example.rikochat.data.remote.mapper.MessageMapper
import com.example.rikochat.data.remote.mapper.UserMapper
import com.example.rikochat.data.remote.model.rest.message.MessageDto
import com.example.rikochat.data.remote.model.rest.user.UserDto
import com.example.rikochat.domain.api.chatSocket.ChatSocketService
import com.example.rikochat.domain.model.message.Message
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.domain.repository.TokenRepository
import com.example.rikochat.utils.DataState
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WebSocketManager(
    private val client: HttpClient,
    private val tokenRepository: TokenRepository,
    private val messageMapper: MessageMapper,
    private val chatRoomMapper: ChatRoomMapper,
    private val userMapper: UserMapper
) {

    private var socket: WebSocketSession? = null

    private val _newMessageFlow = MutableSharedFlow<Message>()
    val newMessageFlow: Flow<Message> = _newMessageFlow.asSharedFlow()

    private val _updateMessageFlow = MutableSharedFlow<Message>()
    val updateMessageFlow: Flow<Message> = _updateMessageFlow.asSharedFlow()

    private val _updateChatRoomMembersFlow = MutableSharedFlow<List<User>>()
    val updateChatRoomMembersFlow: Flow<List<User>> = _updateChatRoomMembersFlow.asSharedFlow()


    suspend fun initSession(): DataState<Unit> {
        return try {
            Log.d("riko", "initSession")
            val token = tokenRepository.getAuthToken()

            if (token.isEmpty()) DataState.Error("Token is empty")

            Log.d("riko", "initSession socket")
            socket = client.webSocketSession(ChatSocketService.Endpoints.ChatSocket.url) {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
            }

            if (socket?.isActive == true) {
                Log.d("riko", "initSession Success")
                DataState.Success(Unit)
            } else {
                Log.d("riko", "initSession Error Couldn't establish connection")
                DataState.Error("Couldn't establish connection")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("riko", "initSession Error ${e.message}")
            DataState.Error(e.localizedMessage ?: "Unexpected error")
        }
    }

    fun isWebSocketConnected(): Boolean {
        return socket != null
    }

    suspend fun observeIncoming() {
        socket?.incoming?.receiveAsFlow()?.map {
            val mappedJson = (it as? Frame.Text)?.readText() ?: ""

            Log.d("riko", "mappedJson $mappedJson")

            val parts = mappedJson.split(":", limit = 2)

            Log.d("riko", "parts $parts")

            when (parts[0]) {
                ChatSocketAction.SendMessage.name -> {
                    val messageDto = Json.decodeFromString<MessageDto>(parts[1])
                    val message = messageMapper.mapFromEntity(messageDto)
                    _newMessageFlow.emit(message)
                }

                ChatSocketAction.UpdateMessage.name -> {
                    val messageDto = Json.decodeFromString<MessageDto>(parts[1])
                    val message = messageMapper.mapFromEntity(messageDto)
                    _updateMessageFlow.emit(message)
                }

                ChatSocketAction.UserAdded.name -> {
                    val usersDto = Json.decodeFromString<List<UserDto>>(parts[1])
                    val users = userMapper.mapFromEntityList(usersDto)
                    _updateChatRoomMembersFlow.emit(users)
                }

                ChatSocketAction.UserRemoved.name -> {
                    val usersDto = Json.decodeFromString<List<UserDto>>(parts[1])
                    val users = userMapper.mapFromEntityList(usersDto)
                    _updateChatRoomMembersFlow.emit(users)
                }
            }
        }?.collect()
    }

    suspend fun sendMessage(message: MessageDto) {
        try {
            val json = Json.encodeToString(message)
            val sendMessage = "${ChatSocketAction.SendMessage.name}:$json"
            socket?.send(Frame.Text(sendMessage))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    override fun observeMessages(): Flow<Message> {
//        return try {
//            socket?.incoming
//                ?.receiveAsFlow()
//                ?.filter {
//                    if (it is Frame.Text) {
//                        val message = it.readText()
//
//                        val parts = message.split(":", limit = 2)
//                        Log.d("riko", "parts[0]: ${parts[0]}")
//                        if (parts[0] != ChatSocketAction.MessageSent.name) return@filter false
//
//                        return@filter true
//
//                    } else return@filter false
//                }?.map {
//                    val mappedJson = (it as? Frame.Text)?.readText() ?: ""
//                    val parts = mappedJson.split(":", limit = 2)
//                    val messageDto = Json.decodeFromString<MessageDto>(parts[1])
//                    messageMapper.mapFromEntity(messageDto)
//                } ?: flow {}
//        } catch (e: Exception) {
//            e.printStackTrace()
//            flow {}
//        }
//    }

    suspend fun sendUpdateMessageLike(
        messageId: String,
        whoLikedUsername: String
    ) {
        try {
            val sendMessage =
                "${ChatSocketAction.UpdateMessage.name}:$whoLikedUsername:$messageId"
            socket?.send(Frame.Text(sendMessage))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    override fun observeUpdateMessageLike(): Flow<Message> {
//        return try {
//            socket?.incoming
//                ?.receiveAsFlow()
//                ?.filter {
//                    if (it is Frame.Text) {
//                        val message = it.readText()
//
//                        val parts = message.split(":", limit = 2)
//
//                        if (parts[0] != ChatSocketAction.UpdateMessageLike.name) return@filter false
//
//                        return@filter true
//
//                    } else return@filter false
//                }?.map {
//                    val mappedJson = (it as? Frame.Text)?.readText() ?: ""
//                    val parts = mappedJson.split(":", limit = 2)
//                    val messageDto = Json.decodeFromString<MessageDto>(parts[1])
//                    messageMapper.mapFromEntity(messageDto)
//                } ?: flow {}
//        } catch (e: Exception) {
//            e.printStackTrace()
//            flow {}
//        }
//    }

    suspend fun updateMessageRead(
        messageId: String,
        ownerUsername: String,
        whoReadUsername: String,
        isRead: Boolean
    ) {
        TODO("Not yet implemented")
    }

//    override fun observeChatRoomMembers(): Flow<List<User>> {
//        return try {
//            socket?.incoming
//                ?.receiveAsFlow()
//                ?.filter {
//                    if (it is Frame.Text) {
//                        val message = it.readText()
//
//                        val parts = message.split(":", limit = 2)
//
//                        if (parts[0] != ChatSocketAction.UserAdded.name || parts[0] != ChatSocketAction.UserRemoved.name)
//                            return@filter false
//
//                        return@filter true
//
//                    } else return@filter false
//                }?.map {
//                    val mappedJson = (it as? Frame.Text)?.readText() ?: ""
//                    val parts = mappedJson.split(":", limit = 2)
//                    val usersDto = Json.decodeFromString<List<UserDto>>(parts[1])
//                    userMapper.mapFromEntityList(usersDto)
//                } ?: flow { }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            flow { }
//        }
//    }

    suspend fun closeSession() {
        socket?.close()
    }

}