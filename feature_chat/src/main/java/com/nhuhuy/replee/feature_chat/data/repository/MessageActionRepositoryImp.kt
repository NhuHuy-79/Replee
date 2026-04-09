package com.nhuhuy.replee.feature_chat.data.repository

import com.nhuhuy.replee.core.database.entity.message_action.ActionType
import com.nhuhuy.replee.feature_chat.data.source.message.MessageActionDataSource
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageAction
import com.nhuhuy.replee.feature_chat.domain.repository.MessageActionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MessageActionRepositoryImp @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val messageActionLocalDataSource: MessageActionDataSource,
) : MessageActionRepository {
    override suspend fun upsertAction(action: MessageAction) {
        return withContext(ioDispatcher) {
            messageActionLocalDataSource.upsertAction(action)
        }
    }

    override suspend fun getActionListWithType(type: ActionType): List<MessageAction> {
        return withContext(ioDispatcher) {
            messageActionLocalDataSource.getActionListWithType(type)
        }
    }

    override suspend fun markActionAsSynced(action: MessageAction) {
        return withContext(ioDispatcher) {
            messageActionLocalDataSource.markActionAsSynced(action)
        }
    }

    override suspend fun getUnSyncedActions(): List<MessageAction> {
        return withContext(ioDispatcher) {
            messageActionLocalDataSource.getMessageActions()
        }
    }

    override suspend fun deleteMessageActionListById(actionIds: List<Long>) {
        return withContext(ioDispatcher) {
            messageActionLocalDataSource.deleteMessageActionListById(actionIds)
        }
    }
}