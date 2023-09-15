package com.example.rikochat.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.rikochat.data.local.entity.chatRoom.ChatRoomAndUsersEntity
import com.example.rikochat.data.local.entity.chatRoom.ChatRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatRoomDao {
    @Query("SELECT * FROM chat_rooms WHERE id = :id")
    suspend fun getChatRoomById(id: String): ChatRoomAndUsersEntity?

    @Query("SELECT * FROM chat_rooms")
    suspend fun getAllChatRooms(): Flow<List<ChatRoomAndUsersEntity>>

    @Query("DELETE FROM chat_rooms")
    suspend fun deleteAllChatRooms()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRoom(chatRoomEntity: ChatRoomEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRooms(chatRooms: List<ChatRoomEntity>)

    @Transaction
    suspend fun replaceAllChatRooms(chatRooms: List<ChatRoomEntity>) {
        deleteAllChatRooms()
        insertChatRooms(chatRooms)
    }

    @Update
    suspend fun updateChatRoom(chatRoomEntity: ChatRoomEntity)

    @Delete
    suspend fun deleteChatRoom(chatRoomEntity: ChatRoomEntity)
}