package com.nhuhuy.replee.core.database.entity.pager

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface MessageRemoteKeyDao {

    @Query("SELECT * FROM message_remote_keys WHERE conversationId = :conversationId LIMIT 1")
    suspend fun get(conversationId: String): MessageRemoteKey?

    @Upsert
    suspend fun upsert(key: MessageRemoteKey)

    @Query("DELETE FROM message_remote_keys WHERE conversationId = :conversationId")
    suspend fun clear(conversationId: String)
}