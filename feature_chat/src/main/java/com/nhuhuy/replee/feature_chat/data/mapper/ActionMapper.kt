package com.nhuhuy.replee.feature_chat.data.mapper

import com.nhuhuy.replee.core.database.entity.message_action.ActionType
import com.nhuhuy.replee.core.database.entity.message_action.MessageActionEntity
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageAction

fun MessageAction.toEntity(): MessageActionEntity {
    return when (this) {
        is MessageAction.Delete -> MessageActionEntity(
            targetId = this.messageId,
            actionType = ActionType.DELETE.name,
            synced = false,
            timestamp = System.currentTimeMillis()
        )

        is MessageAction.MarkAsRead -> MessageActionEntity(
            targetId = this.conversationId,
            actionType = ActionType.MARK_AS_READ.name,
            synced = false,
            timestamp = System.currentTimeMillis()
        )

        is MessageAction.Edit -> MessageActionEntity(
            targetId = this.messageId,
            actionType = ActionType.EDIT.name,
            synced = false,
            timestamp = System.currentTimeMillis()
        )
    }
}

fun MessageActionEntity.toAction(): MessageAction {
    val actionType = ActionType.valueOf(this.actionType)
    return when (actionType) {
        ActionType.DELETE -> MessageAction.Delete(this.targetId)
        ActionType.EDIT -> MessageAction.Edit(this.targetId, this.addingData.orEmpty())
        ActionType.MARK_AS_READ -> MessageAction.MarkAsRead(this.targetId)
    }
}