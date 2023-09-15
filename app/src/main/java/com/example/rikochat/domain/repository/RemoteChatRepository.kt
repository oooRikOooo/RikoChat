package com.example.rikochat.domain.repository

import com.example.rikochat.data.remote.model.rest.chatRoom.CreateChatRoomRequestBody
import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.domain.model.message.Message
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.utils.DataState

interface RemoteChatRepository {
    suspend fun getAllChatRoomMessages(roomId: String): DataState<List<Message>>

    suspend fun getAllUserChatRooms(username: String): DataState<List<ChatRoom>>

    suspend fun createChatRoom(requestBody: CreateChatRoomRequestBody): DataState<ChatRoom>

    suspend fun getChatRoom(roomId: String): DataState<ChatRoom>

    suspend fun getAllChatRoomUsers(roomId: String): DataState<List<User>>

    suspend fun addUserToChatRoom(username: String, roomId: String): DataState<Unit>

    suspend fun deleteUserFromChatRoom(username: String, roomId: String): DataState<Unit>
}