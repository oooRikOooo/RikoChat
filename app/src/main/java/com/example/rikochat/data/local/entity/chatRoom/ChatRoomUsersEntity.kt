package com.example.rikochat.data.local.entity.chatRoom

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_room_users",
    foreignKeys = [
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["chat_room_id"],
            entity = ChatRoomEntity::class,
            onDelete = CASCADE
        )
    ]
)
data class ChatRoomUsersEntity(
    @PrimaryKey @ColumnInfo(name = "chat_room_id") val chatRoomId: String,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "email") val email: String,
)
