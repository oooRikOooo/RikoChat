package com.example.rikochat.data.repository.local

import com.example.rikochat.data.local.entity.chatRoom.ChatRoomUsersEntity
import com.example.rikochat.utils.DataState

interface ChatRoomUsersRepository {
    suspend fun insertChatRoomUser(user: ChatRoomUsersEntity): DataState<Unit>

    suspend fun insertChatRoomUsers(chatRoomUsers: List<ChatRoomUsersEntity>): DataState<Unit>

    suspend fun deleteChatRoomUserByChatRoomId(
        username: String,
        chatRoomId: String
    ): DataState<Unit>

    suspend fun deleteChatRoomUsersByChatRoomId(chatRoomId: String): DataState<Unit>
}