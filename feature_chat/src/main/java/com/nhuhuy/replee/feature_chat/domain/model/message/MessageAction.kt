package com.nhuhuy.replee.feature_chat.domain.model.message

import com.nhuhuy.replee.core.database.entity.message_action.ActionType

data class MessageAction(
    val id: Long = 0,
    val actionType: ActionType,
    val targetId: String,
    val payload: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
