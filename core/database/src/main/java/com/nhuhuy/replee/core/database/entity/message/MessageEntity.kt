package com.nhuhuy.replee.core.database.entity.message

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nhuhuy.replee.core.database.entity.conversation.ConversationEntity

@Entity(
    tableName = "message",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ],
    indices = [Index(value = ["conversationId", "sentAt"])]
)
data class MessageEntity(
    val conversationId: String,
    @PrimaryKey
    val messageId: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val seen: Boolean,
    val sentAt: Long? = null,
    val status: String = "PENDING",
)
