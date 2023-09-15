package com.example.rikochat.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rikochat.data.local.entity.chatRoom.ChatRoomUsersEntity

@Dao
interface ChatRoomUsersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRoomUser(user: ChatRoomUsersEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRoomUsers(chatRoomUsers: List<ChatRoomUsersEntity>)

    @Query("DELETE FROM chat_room_users WHERE  username= :username AND chat_room_id = :chatRoomId")
    suspend fun deleteChatRoomUserByChatRoomId(username: String, chatRoomId: String)

    @Query("DELETE FROM chat_room_users WHERE chat_room_id = :chatRoomId")
    suspend fun deleteChatRoomUsersByChatRoomId(chatRoomId: String)
}