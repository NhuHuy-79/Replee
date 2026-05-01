package com.nhuhuy.replee.core.database.data_source

import com.nhuhuy.replee.core.model.ActionType
import com.nhuhuy.replee.core.database.entity.message_action.ChatActionDao
import com.nhuhuy.replee.core.database.mapper.toAction
import com.nhuhuy.replee.core.database.mapper.toEntity
import com.nhuhuy.replee.core.model.ChatAction
import javax.inject.Inject

interface ChatActionDataSource {
    suspend fun upsertAction(action: ChatAction)
    suspend fun getActionListWithType(type: ActionType): List<ChatAction>
    suspend fun markActionAsSynced(action: ChatAction)
    suspend fun getUnSyncedActions(action: ChatAction): List<ChatAction>
    suspend fun getDeletedActions(): List<ChatAction>
    suspend fun getMessageActions(): List<ChatAction>
    suspend fun getMessageActionsByType(actionType: ActionType): List<ChatAction>
    suspend fun deleteMessageActionListById(actionIds: List<Long>)
}

class ChatActionDataSourceImp @Inject constructor(
    private val chatActionDao: ChatActionDao,
) : ChatActionDataSource {
    override suspend fun upsertAction(action: ChatAction) {
        return chatActionDao.upsertMessageAction(newAction = action.toEntity())
    }

    override suspend fun getActionListWithType(type: ActionType): List<ChatAction> {
        return chatActionDao.getMessageActionListByType(type)
            .map { entity -> entity.toAction() }
    }

    override suspend fun markActionAsSynced(action: ChatAction) {
    }

    override suspend fun getUnSyncedActions(action: ChatAction): List<ChatAction> {
        return chatActionDao.getMessageActionListByType(action.toEntity().actionType)
            .map { entity -> entity.toAction() }
    }

    override suspend fun getDeletedActions(): List<ChatAction> {
        return chatActionDao.getMessageActionListByType(
            type = ActionType.DELETE
        ).map { entity ->
            entity.toAction()
        }
    }

    override suspend fun getMessageActions(): List<ChatAction> {
        return chatActionDao.getMessageActions().map { it.toAction() }
    }

    override suspend fun getMessageActionsByType(actionType: ActionType): List<ChatAction> {
        return chatActionDao.getMessageActionListByType(type = actionType).map { entity ->
            entity.toAction()
        }
    }

    override suspend fun deleteMessageActionListById(actionIds: List<Long>) {
        chatActionDao.deleteMessageActionListById(actionIds)
    }

}
