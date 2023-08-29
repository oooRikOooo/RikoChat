package com.example.rikochat.domain.api.user

import com.example.rikochat.BuildConfig.BASE_URL
import com.example.rikochat.domain.model.chatRoom.ChatRoom
import com.example.rikochat.domain.model.user.User
import com.example.rikochat.utils.DataState

interface UserService {
    suspend fun getUser(username: String): DataState<User>

    suspend fun saveUser(user: User): DataState<Unit>

    suspend fun getUserChatRooms(username: String): DataState<List<ChatRoom>>

    sealed class Endpoints(val url: String) {
        object User : Endpoints("$BASE_URL/user")
        object UserRooms : Endpoints("$BASE_URL/user/rooms")
    }
}