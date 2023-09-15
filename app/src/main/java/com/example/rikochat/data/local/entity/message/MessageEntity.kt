package com.example.rikochat.data.local.entity.message

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "room_id") val roomId: String,
    @ColumnInfo(name = "is_read") val isRead: Boolean,
)
