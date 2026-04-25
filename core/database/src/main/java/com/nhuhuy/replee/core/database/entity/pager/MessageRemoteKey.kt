package com.nhuhuy.replee.core.database.entity.pager

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nhuhuy.replee.core.database.entity.conversation.ConversationEntity

@Entity(
    tableName = "message_remote_keys",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("conversationId")
    ]
)
data class MessageRemoteKey(
    @PrimaryKey
    val conversationId: String,
    val beforeMessageId: String,
    val afterMessageId: String,
    val endReached: Boolean = false,
    val startReached: Boolean = false,
)