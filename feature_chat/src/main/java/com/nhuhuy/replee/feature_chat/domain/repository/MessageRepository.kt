package com.nhuhuy.replee.feature_chat.domain.repository

import androidx.paging.PagingData
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.network.model.DataChange
import com.nhuhuy.replee.feature_chat.domain.model.message.LocalPathMessage
import com.nhuhuy.replee.feature_chat.domain.model.message.Message
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageStatus
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun getNewestMessageInConversation(conversationId: String): Message?
    suspend fun fetchMessagesByTimestamp(
        conversationId: String,
        timestamp: Long
    ): NetworkResult<Unit>
    suspend fun deleteMultipleMessage(messages: List<Message>): NetworkResult<Unit>
    suspend fun getMessageListById(messageIds: List<String>): List<Message>
    suspend fun deleteMessage(message: Message): NetworkResult<String>
    suspend fun sendMessage(message: Message): NetworkResult<String>
    suspend fun saveMessage(message: Message): String
    suspend fun markMessagesAsRead(
        messageIds: List<String>,
        conversationId: String,
        receiverId: String
    ): NetworkResult<Unit>

    fun observeNetworkMessageChange(conversationId: String): Flow<List<DataChange<Message>>>
    suspend fun updateLocalDataChange(
        upsert: List<Message>,
        delete: List<String>
    )

    suspend fun updateRemoteUrlMessage(
        messageId: String,
        remoteUrl: String,
        status: MessageStatus
    ): Message?

    fun observeMessageChangeWithLimit(
        conversationId: String,
        limit: Int = 3
    ): Flow<List<DataChange<Message>>>

    fun observeLocalMessageWithPaging(conversationId: String): Flow<PagingData<LocalPathMessage>>

    suspend fun markAllMessagesRead(
        conversationId: String,
        receiverId: String
    ): NetworkResult<List<Message>>
}
