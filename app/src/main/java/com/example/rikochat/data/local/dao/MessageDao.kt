package com.example.rikochat.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.rikochat.data.local.entity.message.MessageEntity
import com.example.rikochat.data.local.entity.message.MessagesAndUsernamesWhoLikedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Transaction
    suspend fun replaceMessagesByRoomId(roomId: String, newMessages: List<MessageEntity>) {
        deleteMessagesByRoomId(roomId)
        insertMessages(newMessages)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(messageEntity: MessageEntity)

    @Query("SELECT * FROM messages WHERE room_id = :roomId")
    suspend fun getMessagesByRoomId(roomId: String): Flow<List<MessagesAndUsernamesWhoLikedEntity>>

    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getMessageById(id: String): MessagesAndUsernamesWhoLikedEntity?

    @Update
    suspend fun updateMessage(messageEntity: MessageEntity)

    @Query("DELETE FROM messages WHERE room_id = :roomId")
    suspend fun deleteMessagesByRoomId(roomId: String)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessageById(id: String)


}