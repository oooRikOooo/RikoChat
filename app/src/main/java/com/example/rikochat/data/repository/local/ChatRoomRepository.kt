package com.example.rikochat.data.repository.local

import com.example.rikochat.data.local.entity.chatRoom.ChatRoomAndUsersEntity
import com.example.rikochat.data.local.entity.chatRoom.ChatRoomEntity
import com.example.rikochat.utils.DataState
import kotlinx.coroutines.flow.Flow

interface ChatRoomRepository {
    suspend fun getChatRoomById(id: String): DataState<ChatRoomAndUsersEntity?>

    suspend fun getAllChatRooms(): Flow<DataState<List<ChatRoomAndUsersEntity>>>

    suspend fun deleteAllChatRooms(): DataState<Unit>

    suspend fun insertChatRoom(chatRoomEntity: ChatRoomEntity): DataState<Unit>

    suspend fun insertChatRooms(chatRooms: List<ChatRoomEntity>): DataState<Unit>

    suspend fun replaceAllChatRooms(chatRooms: List<ChatRoomEntity>): DataState<Unit>

    suspend fun updateChatRoom(chatRoomEntity: ChatRoomEntity): DataState<Unit>

    suspend fun deleteChatRoom(chatRoomEntity: ChatRoomEntity): DataState<Unit>
}