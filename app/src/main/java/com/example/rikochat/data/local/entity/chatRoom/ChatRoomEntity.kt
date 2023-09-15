package com.example.rikochat.data.local.entity.chatRoom

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_rooms")
data class ChatRoomEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "picture") val picture: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "last_message_timestamp") val lastMessageTimeStamp: Long,
    @ColumnInfo(name = "last_message") val lastMessage: String,
    @ColumnInfo(name = "ownerId") val ownerId: String
)