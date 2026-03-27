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
    @Query("SELECT * FROM message WHERE conversationId = :conversationId")
    suspend fun getMessageByConversationId(conversationId: String): List<MessageEntity>

    @Query("SELECT * FROM message WHERE conversationId = :conversationId " +
            "ORDER BY sentAt DESC")
    fun observeMessageByConversationId(conversationId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM message WHERE messageId = :messageId")
    suspend fun getMessageById(messageId: String): MessageEntity?

    @Query("SELECT * FROM message WHERE status = 'FAILED'")
    suspend fun getFailedMessages() : List<MessageEntity>

    @Query("UPDATE message SET status = :status WHERE messageId in (:messageIds)")
    suspend fun updateStatusOfMessages(messageIds: List<String>, status: String)

    @Query("SELECT * FROM message WHERE type = :messageType & status = 'FAILED'")
    suspend fun getUnSyncedMessageByType(messageType: String): List<MessageEntity>

    @Transaction
    suspend fun upsertAndDeleteMessages(
        upsert: List<MessageEntity>,
        delete: List<String>
    ) {
        upsertAll(upsert)
        deleteMessagesByIds(delete)
    }

    //MarkMessageRead
    @Query("UPDATE message SET seen = 1 WHERE messageId IN (:messageIds) AND conversationId = :conversationId " +
            "AND receiverId = :receiverId")
    suspend fun markMessageAsRead(messageIds: List<String>, conversationId: String, receiverId: String)

    //Update Message Status
    @Query("UPDATE message SET status = :status WHERE messageId = :messageId")
    suspend fun updateStatusOfMessage(messageId: String, status: String)

    @Query("UPDATE message SET status = :status WHERE messageId IN (:messageIds)")
    suspend fun updateStatusOfMessageList(messageIds: List<String>, status: String)


    @Query("DELETE\n" +
            "    FROM message\n" +
            "    WHERE messageId IN (\n" +
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

    @Query(
        """
    SELECT * FROM message 
    WHERE conversationId = :conversationId
    ORDER BY sentAt DESC, messageId DESC
    """
    )
    fun pagingSource(conversationId: String): PagingSource<Int, MessageEntity>

    @Query("DELETE FROM message WHERE conversationId = :conversationId")
    suspend fun clearByConversationId(conversationId: String)

    @Transaction
    @Query("SELECT * FROM message WHERE conversationId = :conversationId ORDER BY sentAt DESC, messageId DESC ")
    fun getMessagesPagingSource(conversationId: String): PagingSource<Int, MessageWithLocalPath>

    @Query(
        """
        SELECT sentAt FROM message
        WHERE conversationId = :conversationId
        ORDER BY sentAt ASC
        LIMIT 1
    """
    )
    suspend fun getOldestCreatedAt(conversationId: String): Long?

    @Query("SELECT * FROM message WHERE conversationId = :conversationId AND content LIKE :query")
    suspend fun getMessageByQuery(conversationId: String, query: String): List<MessageEntity>

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

    @Query("DELETE FROM message WHERE messageId = :messageId")
    suspend fun deleteMessageById(messageId: String)

    @Query("SELECT * FROM message WHERE messageId IN (:messageIds)")
    suspend fun getMessageListById(messageIds: List<String>): List<MessageEntity>

    @Query("UPDATE message SET deleted = 1 WHERE messageId IN (:messageIds)")
    suspend fun softDeleteMessages(messageIds: List<String>)

    @Query("UPDATE message SET deleted = 1 WHERE messageId = :messageId")
    suspend fun softDeleteMessageById(messageId: String)
}

