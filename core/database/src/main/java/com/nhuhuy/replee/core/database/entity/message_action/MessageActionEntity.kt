package com.nhuhuy.replee.core.database.entity.message_action

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_modifier")
data class MessageActionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val targetId: String,
    val actionType: String,
    val addingData: String? = null,
    val synced: Boolean,
    val timestamp: Long
)

