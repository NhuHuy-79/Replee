package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.network.model.DataChange
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun listenConversationWithLimit(
        limit: Int,
        ownerId: String
    ): Flow<List<DataChange<Conversation>>>
    suspend fun fetchOtherUserInConversations(ownerId: String)
    suspend fun fetchConversations(): NetworkResult<List<Conversation>>
    fun observeLocalConversations(ownerId: String): Flow<List<Conversation>>
    suspend fun saveConversations(conversations: List<Conversation>)
    fun observeConversationById(conversationId: String): Flow<Conversation>
    suspend fun getOrCreateConversation(
        ownerId: String,
        otherUserId: String
    ): NetworkResult<String>

    suspend fun updateLocalDataChange(
        dataChanges: List<DataChange<Conversation>>
    ): NetworkResult<Unit>
    suspend fun updateMetadataConversation(
        message: Message
    ): NetworkResult<Unit>

    fun observeOtherUserInConversation(currentUserId: String): Flow<List<String>>
}
