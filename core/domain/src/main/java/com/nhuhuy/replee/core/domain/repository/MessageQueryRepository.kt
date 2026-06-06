package com.nhuhuy.replee.core.domain.repository

import androidx.paging.PagingData
import com.nhuhuy.replee.core.model.chat.LocalPathMessage
import com.nhuhuy.replee.core.model.chat.Message
import kotlinx.coroutines.flow.Flow

interface MessageQueryRepository {
    suspend fun getMessageListById(messageIds: List<String>): List<Message>
    suspend fun getNewestMessageInConversation(conversationId: String): Message?
    fun observePinnedMessages(
        conversationId: String,
        currentUserId: String,
    ): Flow<PagingData<Message>>

    fun observeLocalMessageWithPaging(
        anchorMessageId: String? = null,
        conversationId: String
    ): Flow<PagingData<LocalPathMessage>>

    fun observePinnedMessages(conversationId: String): Flow<List<Message>>
    fun observeMessagesWithQuery(
        currentUserId: String,
        conversationId: String,
        query: String
    ): Flow<PagingData<Message>>
    suspend fun getIndexOfMessage(conversationId: String, messageId: String): Int
    fun listenMessageChanges(conversationId: String): Flow<Unit>
}
