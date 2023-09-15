package com.example.rikochat.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rikochat.data.local.entity.message.UsernamesWhoLikedEntity

@Dao
interface UsernamesWhoLikedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsernamesWhoLiked(usernamesWhoLiked: List<UsernamesWhoLikedEntity>)

    @Query("DELETE FROM usernames_who_liked_message WHERE message_id = :messageId")
    suspend fun deleteUsernameWhoLikedById(messageId: String)

    @Query("DELETE FROM usernames_who_liked_message WHERE message_id IN (:messageIds)")
    suspend fun deleteUsernameWhoLikedByMessageIds(messageIds: List<String>)

}