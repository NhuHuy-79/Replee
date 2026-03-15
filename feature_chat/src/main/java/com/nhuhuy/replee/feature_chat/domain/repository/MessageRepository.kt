package com.nhuhuy.replee.feature_chat.domain.repository

import androidx.paging.PagingData
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.network.model.DataChange
import com.nhuhuy.replee.feature_chat.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun sendMessage(message: Message): NetworkResult<String>
    suspend fun sendImage(rawMessage: Message, uriPath: String): NetworkResult<String>
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

    fun observeMessageChangeWithLimit(
        conversationId: String,
        limit: Int = 3
    ): Flow<List<DataChange<Message>>>

    fun pagingLocalMessages(conversationId: String): Flow<PagingData<Message>>
}
