package com.nhuhuy.replee.feature_chat.data.repository

import com.nhuhuy.replee.feature_chat.data.source.message.MessageActionDataSource
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageAction
import com.nhuhuy.replee.feature_chat.domain.repository.ActionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ActionRepositoryImp @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val actionLocalDataSource: MessageActionDataSource,
) : ActionRepository {
    override suspend fun upsertAction(action: MessageAction) {
        return withContext(ioDispatcher) {
            actionLocalDataSource.upsertAction(action)
        }
    }

    override suspend fun getActionListWithType(action: MessageAction): List<MessageAction> {
        return withContext(ioDispatcher) {
            actionLocalDataSource.getActionListWithType(action)
        }
    }

    override suspend fun markActionAsSynced(action: MessageAction) {
        return withContext(ioDispatcher) {
            actionLocalDataSource.markActionAsSynced(action)
        }
    }

    override suspend fun getUnSyncedActions(action: MessageAction): List<MessageAction> {
        return withContext(ioDispatcher) {
            actionLocalDataSource.getUnSyncedActions(action)
        }
    }

    override suspend fun getDeletedActions(): List<MessageAction> {
        return withContext(ioDispatcher) {
            actionLocalDataSource.getDeletedActions()
        }
    }
}