package com.nhuhuy.replee.core.database.entity.message

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.nhuhuy.replee.core.database.base.BaseDao
import com.nhuhuy.replee.core.database.entity.file_path.MessageWithLocalPath
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao : BaseDao<MessageEntity> {

    // --- READ (Lấy dữ liệu) ---
    @Query("SELECT * FROM message WHERE conversationId = :conversationId AND pinned = 1 ORDER BY sentAt DESC ")
    fun observePinnedMessages(conversationId: String): Flow<List<MessageEntity>>
    @Query("SELECT * FROM message WHERE conversationId = :conversationId")
    suspend fun getMessageByConversationId(conversationId: String): List<MessageEntity>

    @Query("SELECT * FROM message WHERE conversationId = :conversationId ORDER BY sentAt DESC")
    fun observeMessageByConversationId(conversationId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM message WHERE messageId = :messageId")
    suspend fun getMessageById(messageId: String): MessageEntity?

    @Query("SELECT * FROM message WHERE status = 'FAILED'")
    suspend fun getFailedMessages(): List<MessageEntity>

    @Query("SELECT * FROM message WHERE type = :messageType AND status = 'FAILED'")
    suspend fun getUnSyncedMessageByType(messageType: String): List<MessageEntity>

    @Query(
        """
        SELECT * FROM message 
        WHERE conversationId = :conversationId 
        ORDER BY sentAt DESC 
        LIMIT 1
    """
    )
    suspend fun getLatestMessage(conversationId: String): MessageEntity?

    @Transaction
    @Query("SELECT * FROM message WHERE conversationId = :conversationId AND deleted = 0 ORDER BY sentAt DESC")
    fun getMessagesPagingSource(conversationId: String): PagingSource<Int, MessageWithLocalPath>

    @Query("SELECT * FROM message WHERE conversationId = :conversationId AND content LIKE :query")
    suspend fun getMessageByQuery(conversationId: String, query: String): List<MessageEntity>

    @Query("SELECT * FROM message WHERE conversationId = :conversationId AND status = :status")
    suspend fun getMessageByStatus(conversationId: String, status: String): List<MessageEntity>

    @Query("SELECT * FROM message WHERE messageId IN (:messageIds)")
    suspend fun getMessageListById(messageIds: List<String>): List<MessageEntity>

    @Query("SELECT * FROM message WHERE conversationId = :conversationId ORDER BY sentAt DESC LIMIT 1")
    suspend fun getNewestMessageInConversation(conversationId: String): MessageEntity?

    @Query("SELECT * FROM message WHERE conversationId = :conversationId AND content LIKE :query AND type = 'TEXT'")
    fun observeMessagesWithQuery(conversationId: String, query: String): Flow<List<MessageEntity>>


    // --- UPDATE  ---
    @Query("UPDATE message SET pinned = :pinned WHERE messageId = :messageId")
    suspend fun updatePinStatus(messageId: String, pinned: Boolean)
    @Query("UPDATE message SET status = :status WHERE messageId in (:messageIds)")
    suspend fun updateStatusOfMessages(messageIds: List<String>, status: String)

    @Query("UPDATE message SET status = :status WHERE messageId = :messageId")
    suspend fun updateStatusOfMessage(messageId: String, status: String)

    @Query("UPDATE message SET status = :status WHERE messageId IN (:messageIds)")
    suspend fun updateStatusOfMessageList(messageIds: List<String>, status: String)

    @Query("UPDATE message SET content = :remoteUrl WHERE messageId = :messageId")
    suspend fun updateRemoteUrl(messageId: String, remoteUrl: String)

    @Query("UPDATE message SET remoteUrl = :remoteUrl, status = :status WHERE messageId = :messageId")
    suspend fun updateRemoteUrlAndStatus(messageId: String, remoteUrl: String, status: String)

    @Query("UPDATE message SET status = :status WHERE conversationId = :conversationId AND receiverId = :receiverId AND status != :status")
    fun updateMessageStatusInConversation(
        conversationId: String,
        receiverId: String,
        status: String
    )

    @Query("UPDATE message SET status = :status WHERE messageId IN (:messageIds) AND receiverId = :receiverId")
    suspend fun updateMessageListStatus(
        status: String,
        messageIds: List<String>,
        receiverId: String
    )

    @Query("UPDATE message SET deleted = 1 WHERE messageId = :messageId")
    suspend fun softDeleteMessageById(messageId: String)

    @Query("UPDATE message SET deleted = 1 WHERE messageId IN (:messageIds)")
    suspend fun softDeleteAllMessages(messageIds: List<String>)


    // --- DELETE  ---

    @Query(
        "DELETE FROM message WHERE messageId IN (\n" +
            "    SELECT messageId FROM (\n" +
            "    SELECT messageId,\n" +
            "    ROW_NUMBER() OVER (\n" +
            "    PARTITION BY conversationId\n" +
            "    ORDER BY sentAt DESC\n" +
            "    ) AS rn\n" +
            "    FROM message\n" +
            "    )\n" +
            "    WHERE rn > :limit\n" +
            "    )\n"
    )
    suspend fun deleteMessageByConversationId(limit: Int)

    @Query("DELETE FROM message WHERE messageId IN (:messageIds)")
    suspend fun deleteMessagesByIds(messageIds: List<String>)

    @Query("DELETE FROM message WHERE deleted = 1")
    suspend fun deleteAllMessages()

    @Query("DELETE FROM message WHERE conversationId = :conversationId")
    suspend fun clearByConversationId(conversationId: String)

    @Query("DELETE FROM message WHERE messageId = :messageId")
    suspend fun deleteMessageById(messageId: String)


    // --- TRANSACTION / MIXED  ---

    @Transaction
    suspend fun upsertAndDeleteMessages(
        networkMessages: List<MessageEntity>,
        deleteIds: List<String>
    ) {
        if (deleteIds.isNotEmpty()) {
            deleteMessagesByIds(deleteIds)
        }

        networkMessages.forEach { networkMsg ->
            val localMsg = getMessageById(networkMsg.messageId)

            if (localMsg == null) {
                upsert(networkMsg)
            } else {
                val updatedMsg = networkMsg.copy(
                    deleted = localMsg.deleted,
                    localUriPath = localMsg.localUriPath
                )
                upsert(updatedMsg)
            }
        }
    }
}
