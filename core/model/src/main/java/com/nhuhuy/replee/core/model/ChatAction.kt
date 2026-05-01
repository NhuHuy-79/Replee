package com.nhuhuy.replee.core.model

data class ChatAction(
    val id: Long = 0,
    val actionType: ActionType,
    val targetId: String,
    val payload: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
