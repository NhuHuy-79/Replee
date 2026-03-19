package com.nhuhuy.replee.core.database.entity.conversation

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.nhuhuy.replee.core.database.base.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao : BaseDao<ConversationEntity> {

    @Query("Update conversation SET unreadMessageCount = 0 WHERE id = :conversationId")
    suspend fun clearUnreadMessages(conversationId: String)

    @Transaction
    @Query("SELECT * FROM conversation WHERE id = :id")
    suspend fun getConversationById(id: String): ConversationAndUser?

    @Query("DELETE FROM conversation WHERE id in (:list)")
    suspend fun deleteConversationsById(list: List<String>)

    @Query("SELECT otherUserId FROM conversation WHERE ownerId = :uid")
    suspend fun getOtherUserInConversationList(uid: String): List<String>

    @Query("SELECT DISTINCT otherUserId FROM conversation WHERE ownerId = :uid")
    fun getOtherUserInConversationFlow(uid: String): Flow<List<String>>

    @Transaction
    suspend fun upsertAndDeleteConversations(
        upsert: List<ConversationEntity>,
        delete: List<String>,
    ) {
        upsertAll(upsert)
        deleteConversationsById(delete)
    }

    @Query("DELETE FROM conversation WHERE ownerId = :uid")
    suspend fun deleteConversationsByOwnerId(uid: String)

    @Transaction
    @Query("SELECT * FROM conversation WHERE id = :id")
    fun observeConversationById(id: String): Flow<ConversationAndUser?>

    @Query("DELETE FROM conversation WHERE id = :id")
    suspend fun deleteConversationById(id: String)

    @Transaction
    @Query("SELECT * FROM conversation WHERE ownerId = :uid ORDER BY lastMessageTime DESC")
    fun observeConversations(uid: String): Flow<List<ConversationAndUser>>

    @Query("SELECT COUNT(*) FROM conversation WHERE ownerId = :ownerId")
    suspend fun getConversationListCount(ownerId: String) : Int

    @Query("UPDATE conversation SET lastTimeSyncs = :lastSyncedTime WHERE id in (:conversationIds)")
    suspend fun updateSyncedTime(conversationIds: List<String>, lastSyncedTime: Long)

    @Transaction
    @Query("SELECT * FROM conversation WHERE id = :id")
    suspend fun getConversationAndUserById(id: String): ConversationAndUser?

    @Query(
        "UPDATE conversation SET " +
                "lastMessageContent = :lastMessageContent, " +
                "lastSenderId = :lastSenderId, " +
                "lastMessageTime = :lastMessageTime, " +
                "lastMessageType = :lastMessageType " +
                "WHERE id = :conversationId"
    )
    suspend fun updateLastMessage(
        conversationId: String,
        lastMessageContent: String,
        lastSenderId: String,
        lastMessageTime: Long,
        lastMessageType: String,
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
    @Query(
        """
    SELECT 
    c.*,
    o.uid AS owner_uid, o.name AS owner_name, o.imageUrl AS owner_imageUrl, o.isOnline AS owner_isOnline,
    u.uid AS other_uid, u.name AS other_name, u.imageUrl AS other_imageUrl, u.isOnline AS other_isOnline
    FROM conversation c
    LEFT JOIN accounts o ON c.ownerId = o.uid
    LEFT JOIN accounts u ON c.otherUserId = u.uid
    WHERE c.synced = 0
    """
    )
    suspend fun getUnSyncedConversation(): List<ConversationAndUser>

    @Query("UPDATE conversation SET synced = :synced WHERE id in (:conversations)")
    suspend fun updateSyncedStatusOfConversations(conversations: List<String>, synced: Boolean)

    @Query("UPDATE conversation SET ownerNick = :ownerNick WHERE id = :conversationId")
    suspend fun updateOwnerNickname(conversationId: String, ownerNick: String)

    @Query("UPDATE conversation SET otherUserNick = :otherUserNick WHERE id = :conversationId")
    suspend fun updateOtherUserNickname(conversationId: String, otherUserNick: String)

}
