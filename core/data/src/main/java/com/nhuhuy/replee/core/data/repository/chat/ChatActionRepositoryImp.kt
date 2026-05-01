package com.nhuhuy.replee.core.data.repository.chat

import com.nhuhuy.replee.core.common.utils.IoDispatcher
import com.nhuhuy.replee.core.database.data_source.ChatActionDataSource
import com.nhuhuy.replee.core.domain.repository.ChatActionRepository
import com.nhuhuy.replee.core.model.chat.ActionType
import com.nhuhuy.replee.core.model.chat.ChatAction
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
