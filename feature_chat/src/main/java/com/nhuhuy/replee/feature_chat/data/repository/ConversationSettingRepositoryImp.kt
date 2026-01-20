package com.nhuhuy.replee.feature_chat.data.repository

import com.nhuhuy.replee.feature_chat.data.data_store.SeedColor
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationSettingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ConversationSettingRepositoryImp @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val conversationLocalDataSource: ConversationLocalDataSource
) : ConversationSettingRepository {
    override suspend fun updateSeedColor(seedColor: SeedColor) {
        return withContext(dispatcher) {
            TODO("Update seed color")
        }
    }

    override suspend fun muteOtherUser(conversationId: String) {
        return withContext(dispatcher) {
            conversationLocalDataSource.updateMutedStatus(conversationId)
        }
    }

    override suspend fun pinConversation(conversationId: String) {
        return withContext(dispatcher) {
            conversationLocalDataSource.updatePinnedStatus(conversationId)
        }
    }

    override suspend fun blockOtherUser(conversationId: String) {
        return withContext(dispatcher) {
            conversationLocalDataSource.updateBlockStatus(conversationId)
        }
    }

    override suspend fun deleteConversation(conversationId: String) {
        return withContext(dispatcher) {
            conversationLocalDataSource.updateDeleteStatus(conversationId)
        }
    }

}