package com.example.rikochat.data.local.entity.message

import androidx.room.Embedded
import androidx.room.Relation

data class MessagesAndUsernamesWhoLikedEntity(
    @Embedded val message: MessageEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "message_id",

        ) val usernamesWhoLiked: List<UsernamesWhoLikedEntity>
)