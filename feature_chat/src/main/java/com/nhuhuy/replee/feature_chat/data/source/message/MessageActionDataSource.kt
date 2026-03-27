package com.nhuhuy.replee.feature_chat.data.source.message

import com.nhuhuy.replee.core.database.entity.message_action.ActionType
import com.nhuhuy.replee.core.database.entity.message_action.MessageActionDao
import com.nhuhuy.replee.feature_chat.data.mapper.toAction
import com.nhuhuy.replee.feature_chat.data.mapper.toEntity
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageAction
import jakarta.inject.Inject

interface MessageActionDataSource {
    suspend fun upsertAction(action: MessageAction)
    suspend fun getActionListWithType(action: MessageAction): List<MessageAction>
    suspend fun markActionAsSynced(action: MessageAction)
    suspend fun getUnSyncedActions(action: MessageAction): List<MessageAction>

    suspend fun getDeletedActions(): List<MessageAction>
}

class MessageActionDataSourceImp @Inject constructor(
    private val messageActionDao: MessageActionDao,
) : MessageActionDataSource {
    override suspend fun upsertAction(action: MessageAction) {
        return messageActionDao.upsert(action.toEntity())
    }

    override suspend fun getActionListWithType(action: MessageAction): List<MessageAction> {
        return messageActionDao.getMessageActionListByType(action.toEntity().actionType)
            .map { entity -> entity.toAction() }
    }

    override suspend fun markActionAsSynced(action: MessageAction) {
        return messageActionDao.markActionAsSyncedByType(action.toEntity().actionType)
    }

    override suspend fun getUnSyncedActions(action: MessageAction): List<MessageAction> {
        return messageActionDao.getMessageActionListByType(action.toEntity().actionType)
            .map { entity -> entity.toAction() }
    }

    override suspend fun getDeletedActions(): List<MessageAction> {
        return messageActionDao.getMessageActionListByType(
            type = ActionType.DELETE.name
        ).map { entity ->
            entity.toAction()
        }
    }

}