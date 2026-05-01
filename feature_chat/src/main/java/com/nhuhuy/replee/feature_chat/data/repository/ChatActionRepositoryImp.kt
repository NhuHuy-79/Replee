package com.nhuhuy.replee.feature_chat.data.repository

import com.nhuhuy.replee.core.data.utils.IoDispatcher
import com.nhuhuy.replee.core.database.entity.message_action.ActionType
import com.nhuhuy.replee.feature_chat.data.source.message.ChatActionDataSource
import com.nhuhuy.replee.feature_chat.domain.model.message.ChatAction
import com.nhuhuy.replee.feature_chat.domain.repository.ChatActionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatActionRepositoryImp @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val chatActionLocalDataSource: ChatActionDataSource,
) : ChatActionRepository {
    override suspend fun upsertAction(action: ChatAction) {
        return withContext(ioDispatcher) {
            chatActionLocalDataSource.upsertAction(action)
        }
    }

    override suspend fun getActionListWithType(type: ActionType): List<ChatAction> {
        return withContext(ioDispatcher) {
            chatActionLocalDataSource.getActionListWithType(type)
        }
    }

    override suspend fun markActionAsSynced(action: ChatAction) {
        return withContext(ioDispatcher) {
            chatActionLocalDataSource.markActionAsSynced(action)
        }
    }

    override suspend fun getUnSyncedActions(): List<ChatAction> {
        return withContext(ioDispatcher) {
            chatActionLocalDataSource.getMessageActions()
        }
    }

    override suspend fun deleteMessageActionListById(actionIds: List<Long>) {
        return withContext(ioDispatcher) {
            chatActionLocalDataSource.deleteMessageActionListById(actionIds)
        }
    }
}
