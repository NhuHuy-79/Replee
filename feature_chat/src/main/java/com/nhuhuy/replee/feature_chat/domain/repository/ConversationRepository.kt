package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.model.converastion.Conversation
import com.nhuhuy.replee.feature_chat.domain.model.message.Message
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun listenConversationWithLimit(
        limit: Int,
        ownerId: String
    ): Flow<Unit>
    suspend fun fetchOtherUserInConversations(ownerId: String)
    suspend fun fetchConversations(): NetworkResult<List<Conversation>>
    fun observeLocalConversationList(ownerId: String): Flow<List<Conversation>>
    suspend fun saveConversations(conversations: List<Conversation>)
    suspend fun getOrCreateConversation(
        ownerId: String,
        otherUserId: String
    ): NetworkResult<String>

    suspend fun updateMetadataConversation(
        message: Message
    ): NetworkResult<Unit>

    suspend fun getConversationById(conversationId: String): Conversation

    fun observeOtherUserInConversation(currentUserId: String): Flow<List<String>>
    suspend fun markAllMessagesRead(
        conversationId: String,
        currentUserId: String
    ): NetworkResult<Unit>

    suspend fun deleteMetadataLastMessage(message: Message): NetworkResult<String>
}
