package com.nhuhuy.replee.core.database.entity.message_action

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.nhuhuy.replee.core.database.base.BaseDao

@Dao
interface MessageActionDao : BaseDao<MessageActionEntity> {
    @Query("SELECT * FROM message_modifier WHERE targetId = :targetId AND actionType = :actionType LIMIT 1")
    suspend fun getAction(targetId: String, actionType: ActionType): MessageActionEntity?

    @Insert
    suspend fun insertAction(action: MessageActionEntity)

    @Query("UPDATE message_modifier SET payload = :newPayload, timestamp = :newTimestamp WHERE id = :id")
    suspend fun updatePayload(id: Long, newPayload: String?, newTimestamp: Long)

    @Transaction
    suspend fun upsertMessageAction(newAction: MessageActionEntity) {
        val existingAction = getAction(newAction.targetId, newAction.actionType)

        if (existingAction != null) {
            updatePayload(
                id = existingAction.id,
                newPayload = newAction.payload,
                newTimestamp = newAction.timestamp
            )
        } else {
            insertAction(newAction)
        }
    }

    @Query("SELECT * FROM message_modifier WHERE actionType = :type")
    suspend fun getMessageActionListByType(type: ActionType): List<MessageActionEntity>

    @Query("DELETE FROM message_modifier")
    suspend fun deleteAllSyncedActions()

    @Query("DELETE FROM message_modifier WHERE id IN (:actionIds)")
    suspend fun deleteMessageActionListById(actionIds: List<Long>)
}