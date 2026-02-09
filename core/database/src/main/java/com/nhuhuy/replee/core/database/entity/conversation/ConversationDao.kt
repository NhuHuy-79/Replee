package com.nhuhuy.replee.core.database.entity.conversation

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.nhuhuy.replee.core.database.base.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao : BaseDao<ConversationEntity> {

    @Transaction
    @Query("SELECT * FROM conversation WHERE id = :id")
    suspend fun getConversationById(id: String): ConversationAndUser?

    @Transaction
    @Query("SELECT * FROM conversation WHERE id = :id")
    fun observeConversationById(id: String): Flow<ConversationAndUser?>

    @Query("DELETE FROM conversation WHERE id = :id")
    suspend fun deleteConversationById(id: String)
    @Transaction
    @Query(
        """
    SELECT * FROM conversation
    WHERE ownerId = :ownerId
    ORDER BY pinned DESC, lastMessageTime DESC
"""
    )
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

    @Query("UPDATE conversation SET muted = :muted WHERE id = :conversationId")
    suspend fun updateMutedStatus(conversationId: String, muted: Boolean)

    @Query("UPDATE conversation SET deleted = :deleted WHERE id = :conversationId")
    suspend fun updateDeleteStatus(conversationId: String, deleted: Boolean)

    @Query("UPDATE conversation SET blocked = :blocked WHERE id = :conversationId")
    suspend fun updateBlockStatus(conversationId: String, blocked: Boolean)

    @Query("UPDATE conversation SET pinned = :pinned WHERE id = :conversationId")
    suspend fun updatePinnedStatus(conversationId: String, pinned: Boolean)

    @Query("UPDATE conversation SET synced = :synced WHERE id = :conversationId")
    suspend fun updateSyncedStatus(conversationId: String, synced: Boolean)

    @Transaction
    @Query("SELECT * from conversation WHERE synced = 0")
    suspend fun getUnSyncedConversation(): List<ConversationAndUser>

    @Query("UPDATE conversation SET synced = :synced WHERE id in (:conversations)")
    suspend fun updateSyncedStatusOfConversations(conversations: List<String>, synced: Boolean)

    @Query("UPDATE conversation SET ownerNick = :ownerNick WHERE id = :conversationId")
    suspend fun updateOwnerNickname(conversationId: String, ownerNick: String)

    @Query("UPDATE conversation SET otherUserNick = :otherUserNick WHERE id = :conversationId")
    suspend fun updateOtherUserNickname(conversationId: String, otherUserNick: String)

}