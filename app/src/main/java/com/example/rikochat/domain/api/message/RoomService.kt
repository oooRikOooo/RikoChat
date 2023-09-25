package com.example.rikochat.domain.api.message

import com.example.rikochat.BuildConfig.BASE_URL
import com.example.rikochat.data.remote.model.rest.chatRoom.CreateChatRoomRequestBody
import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.domain.model.message.Message
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.utils.DataState

interface RoomService {

    suspend fun getAllChatRoomMessages(roomId: String): DataState<List<Message>>

    suspend fun getAllUserChatRooms(): DataState<List<ChatRoom>>

    suspend fun createChatRoom(requestBody: CreateChatRoomRequestBody): DataState<ChatRoom>

    suspend fun getChatRoom(roomId: String): DataState<ChatRoom>

    suspend fun getAllChatRoomUsers(roomId: String): DataState<List<User>>

    suspend fun addUserToChatRoom(username: String, roomId: String): DataState<Unit>

    suspend fun deleteUserFromChatRoom(userId: String, roomId: String): DataState<Unit>

    sealed class Endpoints(val url: String) {
        object GetAllChatRoomMessages : Endpoints("$BASE_URL/room/messages")

        object GetAllUserChatRooms : Endpoints("$BASE_URL/user/rooms")

        object CreateChatRoom : Endpoints("$BASE_URL/room")

        object GetChatRoom : Endpoints("$BASE_URL/room")

        object GetAllChatRoomUsers : Endpoints("$BASE_URL/room/users")

        object AddUserToChatRoom : Endpoints("$BASE_URL/room/addUser")

        object DeleteUserFromChatRoom : Endpoints("$BASE_URL/room/deleteUser")
    }
}