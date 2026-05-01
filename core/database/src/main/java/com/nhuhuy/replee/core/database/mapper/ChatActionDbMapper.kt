package com.nhuhuy.replee.core.database.mapper

import com.nhuhuy.replee.core.database.entity.message_action.ChatActionEntity
import com.nhuhuy.replee.core.model.ChatAction

fun ChatAction.toEntity(): ChatActionEntity {
    return ChatActionEntity(
        targetId = targetId,
        actionType = actionType,
        payload = payload,
        timestamp = timestamp
    )
}

fun ChatActionEntity.toAction(): ChatAction {
    return ChatAction(
        id = id,
        targetId = targetId,
        actionType = actionType,
        payload = payload,
        timestamp = timestamp
    )
}
