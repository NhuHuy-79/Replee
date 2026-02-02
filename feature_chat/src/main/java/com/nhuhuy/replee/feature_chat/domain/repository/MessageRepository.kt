package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.replee.core.common.error_handling.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun sendMessage(message: Message): NetworkResult<String>
    fun observeNetworkMessageList(conversationId: String): Flow<NetworkResult<List<Message>>>
    fun observeLocalMessages(conversationId: String) : Flow<List<Message>>
    suspend fun markMessagesAsRead(
        messageIds: List<String>,
        conversationId: String,
        receiverId: String
    ): NetworkResult<Unit>
    suspend fun saveMessages(messages: List<Message>)
    suspend fun searchMessageWithQuery(conversationId: String, query: String) : List<Message>
}
