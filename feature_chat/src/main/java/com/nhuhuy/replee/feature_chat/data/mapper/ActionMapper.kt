package com.nhuhuy.replee.feature_chat.data.mapper

import com.nhuhuy.replee.core.database.entity.message_action.MessageActionEntity
import com.nhuhuy.replee.feature_chat.domain.model.message.MessageAction

fun MessageAction.toEntity(): MessageActionEntity {
    return MessageActionEntity(
        targetId = targetId,
        actionType = actionType,
        payload = payload,
        timestamp = timestamp
    )
}

fun MessageActionEntity.toAction(): MessageAction {
    return MessageAction(
        id = id,
        targetId = targetId,
        actionType = actionType,
        payload = payload,
        timestamp = timestamp
    )
}