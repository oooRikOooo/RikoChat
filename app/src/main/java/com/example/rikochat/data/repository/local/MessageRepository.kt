package com.example.rikochat.data.repository.local

import com.example.rikochat.data.local.entity.message.MessageEntity
import com.example.rikochat.data.local.entity.message.MessagesAndUsernamesWhoLikedEntity
import com.example.rikochat.utils.DataState
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun replaceMessagesByRoomId(
        roomId: String,
        newMessages: List<MessageEntity>
    ): DataState<Unit>

    suspend fun insertMessages(messages: List<MessageEntity>): DataState<Unit>

    suspend fun insertMessage(messageEntity: MessageEntity): DataState<Unit>

    suspend fun getMessagesByRoomId(roomId: String): Flow<DataState<List<MessagesAndUsernamesWhoLikedEntity>>>

    suspend fun getMessageById(id: String): DataState<MessagesAndUsernamesWhoLikedEntity?>

    suspend fun updateMessage(messageEntity: MessageEntity): DataState<Unit>

    suspend fun deleteMessagesByRoomId(roomId: String): DataState<Unit>

    suspend fun deleteMessageById(id: String): DataState<Unit>

}