package com.nhuhuy.replee.core.domain.repository

import com.nhuhuy.replee.core.model.chat.Conversation
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.error_handling.NetworkResult

interface ConversationActionRepository {
    suspend fun saveConversations(conversations: List<Conversation>)
    suspend fun getOrCreateConversation(
        ownerId: String,
        otherUserId: String
    ): NetworkResult<String>

    suspend fun updateMetadataConversation(
        message: Message
    ): NetworkResult<Unit>

    suspend fun markAllMessagesRead(
        conversationId: String,
        currentUserId: String
    ): NetworkResult<Unit>

    suspend fun deleteMetadataLastMessage(message: Message): NetworkResult<String>

    suspend fun deleteConversation(id: String): NetworkResult<Unit>
    suspend fun deleteMultipleConversations(ids: List<String>): NetworkResult<Unit>
}
