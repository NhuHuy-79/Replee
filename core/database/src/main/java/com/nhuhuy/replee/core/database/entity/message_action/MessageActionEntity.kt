package com.nhuhuy.replee.core.database.entity.message_action

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "message_modifier",
    indices = [
        Index(value = ["targetId"])
    ]
)
data class MessageActionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val targetId: String,
    val actionType: ActionType,
    val payload: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

