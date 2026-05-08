package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.replee.core.model.chat.LocalPathMessage
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import kotlinx.coroutines.flow.Flow

interface PaginatorRepository {
    fun observeLocalMessageAroundKey(key: String, conversationId: String, limit: Int):
            Flow<List<LocalPathMessage>>

    fun observeMessagesAround(
        conversationId: String,
        startTime: Long,
        endTime: Long
    ): Flow<List<LocalPathMessage>>

    fun observeLocalMessages(conversationId: String): Flow<List<LocalPathMessage>>
    suspend fun getCurrentKey(conversationId: String): String?
    suspend fun fetchLatestMessage(
        conversationId: String,
        limit: Long
    ): NetworkResult<List<Message>>

    suspend fun fetchMessageBeforeKey(
        conversationId: String,
        key: String,
        limit: Long
    ): NetworkResult<List<Message>>

    suspend fun fetchMessageAfterKey(
        conversationId: String,
        key: String,
        limit: Long
    ): NetworkResult<List<Message>>

    suspend fun fetchMessageBetweenKey(
        conversationId: String,
        key: String,
        limit: Long
    ): NetworkResult<List<Message>>
}