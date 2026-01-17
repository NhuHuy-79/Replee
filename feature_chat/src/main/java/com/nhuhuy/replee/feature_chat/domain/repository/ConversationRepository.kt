package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import kotlinx.coroutines.flow.Flow


interface ConversationRepository {
    suspend fun fetchConversationList() : Resource<List<Conversation>, RemoteFailure>
    suspend fun getConversationListCount() : Int
    fun observeConversationList() : Flow<List<Conversation>>
    suspend fun saveConversationToLocal(conversations: List<Conversation>)
    fun listenFromNetwork() : Flow<Resource<List<Conversation>, RemoteFailure>>
    suspend fun getOrCreateConversation(otherUser: Account) : Resource<String, RemoteFailure>
}
