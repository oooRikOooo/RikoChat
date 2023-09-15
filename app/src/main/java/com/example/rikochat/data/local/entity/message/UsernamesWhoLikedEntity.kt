package com.example.rikochat.data.local.entity.message

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "usernames_who_liked_message",
    foreignKeys = [
        ForeignKey(
            entity = MessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["message_id"],
            onDelete = CASCADE
        )
    ]
)
data class UsernamesWhoLikedEntity(
    @PrimaryKey @ColumnInfo(name = "message_id") val message_id: String,
    @ColumnInfo(name = "username") val username: String
)