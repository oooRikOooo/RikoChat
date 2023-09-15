package com.example.rikochat.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rikochat.data.local.dao.ChatRoomDao
import com.example.rikochat.data.local.dao.ChatRoomUsersDao
import com.example.rikochat.data.local.dao.MessageDao
import com.example.rikochat.data.local.dao.UsernamesWhoLikedDao
import com.example.rikochat.data.local.entity.chatRoom.ChatRoomEntity
import com.example.rikochat.data.local.entity.chatRoom.ChatRoomUsersEntity
import com.example.rikochat.data.local.entity.message.MessageEntity
import com.example.rikochat.data.local.entity.message.UsernamesWhoLikedEntity

@Database(
    entities = [MessageEntity::class, UsernamesWhoLikedEntity::class, ChatRoomEntity::class, ChatRoomUsersEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract val messageDao: MessageDao

    abstract val usernamesWhoLikedDao: UsernamesWhoLikedDao

    abstract val chatRoomDao: ChatRoomDao

    abstract val chatRoomUsersDao: ChatRoomUsersDao
}