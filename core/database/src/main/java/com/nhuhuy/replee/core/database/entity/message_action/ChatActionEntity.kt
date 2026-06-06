package com.nhuhuy.replee.core.database.entity.message_action

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nhuhuy.replee.core.model.chat.ActionType

@Entity(
    tableName = "message_modifier",
    indices = [
        Index(value = ["targetId"])
    ]
)
data class ChatActionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val targetId: String,
    val actionType: ActionType,
    val payload: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

