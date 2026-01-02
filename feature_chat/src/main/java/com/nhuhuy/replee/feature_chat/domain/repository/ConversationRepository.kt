package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import kotlinx.coroutines.flow.Flow


interface ConversationRepository {
    fun observeConversationList() : Flow<Resource<List<Conversation>, RemoteFailure>>
    suspend fun addConversation(conversation: Conversation) : Resource<Unit, RemoteFailure>
}
