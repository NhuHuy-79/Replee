package com.nhuhuy.replee.core.database.entity.conversation

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.nhuhuy.replee.core.database.base.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao : BaseDao<ConversationEntity> {

    @Query("SELECT * FROM conversation WHERE id = :id")
    suspend fun getConversationById(id: String): ConversationEntity?


    @Transaction
    @Query("SELECT * FROM conversation WHERE ownerId = :ownerId ORDER BY lastMessageTime DESC")
    fun observeConversations(ownerId: String): Flow<List<ConversationAndUser>>

    @Query("SELECT COUNT(*) FROM conversation WHERE ownerId = :ownerId")
    suspend fun getConversationListCount(ownerId: String) : Int

    @Query("UPDATE conversation SET lastMessageTime = :lastMessageTime WHERE id in (:conversationIds)")
    suspend fun updateSyncedTime(conversationIds: List<String>, lastMessageTime: Long)
    @Transaction
    @Query("SELECT * FROM conversation WHERE id = :id")
    suspend fun getConversationAndUserById(id: String): ConversationAndUser?

    @Query("UPDATE conversation SET" +
            " lastMessageContent = :lastMessageContent, " +
            "lastSenderId = :lastSenderId, " +
            "lastMessageTime = :lastMessageTime " +
            "WHERE id = :conversationId")
    suspend fun updateLastMessage(
        conversationId: String,
        lastMessageContent: String,
        lastSenderId: String,
        lastMessageTime: Long,
    )

}