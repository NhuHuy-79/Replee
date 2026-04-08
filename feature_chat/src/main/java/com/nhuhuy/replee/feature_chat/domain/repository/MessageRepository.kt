package com.nhuhuy.replee.feature_chat.domain.repository

import androidx.paging.PagingData
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.model.message.LocalPathMessage
import com.nhuhuy.replee.feature_chat.domain.model.message.Message
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageStatus
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    // --- CREATE / SEND ---
    suspend fun sendMessage(message: Message): NetworkResult<String>
    suspend fun saveMessage(message: Message): String

    // --- READ / OBSERVE ---
    suspend fun getMessageListById(messageIds: List<String>): List<Message>
    suspend fun getNewestMessageInConversation(conversationId: String): Message?
    fun observeLocalMessageWithPaging(conversationId: String): Flow<PagingData<LocalPathMessage>>
    fun observeLocalMessagesWithQuery(conversationId: String, query: String): Flow<List<Message>>

    // --- UPDATE ---
    suspend fun updateRemoteUrlMessage(
        messageId: String,
        remoteUrl: String,
        status: MessageStatus
    ): Message?

    // --- DELETE ---
    suspend fun deleteMessage(message: Message): NetworkResult<String>
    suspend fun deleteMultipleMessage(messages: List<Message>): NetworkResult<Unit>

    // --- SYNC / NETWORK---
    suspend fun fetchMessagesByTimestamp(
        conversationId: String,
        timestamp: Long
    ): NetworkResult<Unit>

    fun listenMessageChanges(conversationId: String): Flow<Unit>
}
