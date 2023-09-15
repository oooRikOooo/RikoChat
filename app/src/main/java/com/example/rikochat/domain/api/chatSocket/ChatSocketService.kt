package com.example.rikochat.domain.api.chatSocket

import com.example.rikochat.BuildConfig.BASE_URL
import com.example.rikochat.data.remote.model.rest.message.MessageDto
import com.example.rikochat.utils.DataState

interface ChatSocketService {

    suspend fun initSession(username: String): DataState<Unit>

    fun isWebSocketConnected(): Boolean

    suspend fun sendMessage(message: MessageDto)

//    fun observeMessages(): Flow<Message>

    suspend fun sendUpdateMessageLike(
        messageId: String,
        ownerUsername: String,
        whoLikedUsername: String,
        isLiked: Boolean
    )

    suspend fun updateMessageRead(
        messageId: String,
        ownerUsername: String,
        whoReadUsername: String,
        isRead: Boolean
    )

//    fun observeUpdateMessageLike(): Flow<Message>

//    fun observeRooms(): Flow<List<ChatRoom>>

//    fun observeChatRoomsDetails(roomId: String): Flow<ChatRoom>

//    fun observeChatRoomMembers(): Flow<List<User>>

    suspend fun closeSession()

    sealed class Endpoints(val url: String) {
        object ChatSocket : Endpoints("$BASE_URL/chat-socket")
    }
}