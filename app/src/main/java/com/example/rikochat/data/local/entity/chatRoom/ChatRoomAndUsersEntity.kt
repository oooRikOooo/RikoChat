package com.example.rikochat.data.local.entity.chatRoom

import androidx.room.Embedded
import androidx.room.Relation

data class ChatRoomAndUsersEntity(
    @Embedded val chatRoom: ChatRoomEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "chat_room_id"
    )
    val users: List<ChatRoomUsersEntity>
)