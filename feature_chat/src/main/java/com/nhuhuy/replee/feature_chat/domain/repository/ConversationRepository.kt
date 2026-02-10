package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.firebase.model.DataChange
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    suspend fun fetchOtherUserInConversations(ownerId: String)
    suspend fun fetchConversations(): NetworkResult<List<Conversation>>
    fun observeLocalConversations() : Flow<List<Conversation>>
    suspend fun saveConversations(conversations: List<Conversation>)
    fun observeConversationById(conversationId: String): Flow<Conversation>
    fun observeNetworkConversation(): Flow<NetworkResult<List<Conversation>>>
    suspend fun getOrCreateConversation(otherUser: Account): NetworkResult<String>
    fun observeNetworkConversationChange(ownerId: String): Flow<List<DataChange<Conversation>>>

    suspend fun updateLocalDataChange(
        upsert: List<Conversation>,
        delete: List<String>
    )
}
