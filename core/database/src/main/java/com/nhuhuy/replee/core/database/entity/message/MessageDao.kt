package com.nhuhuy.replee.core.database.entity.message

import androidx.room.Dao
import androidx.room.Query
import com.nhuhuy.replee.core.database.base.BaseDao
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

    //Sync message with limit()

    //MarkMessageRead
    @Query("UPDATE message SET seen = 1 WHERE messageId IN (:messageIds) AND conversationId = :conversationId " +
            "AND receiverId = :receiverId")
    suspend fun markMessageAsRead(messageIds: List<String>, conversationId: String, receiverId: String)

}

