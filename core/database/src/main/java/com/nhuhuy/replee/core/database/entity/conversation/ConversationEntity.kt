package com.nhuhuy.replee.core.database.entity.conversation

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "conversation",
    indices = [
        Index(value = ["ownerId", "pinned", "lastMessageTime"]),
        Index(value = ["otherUserId"]),
        Index(value = ["synced"])
    ]
)
data class ConversationEntity(
    @PrimaryKey
    val id: String = "",
    val ownerId: String = "",
    val otherUserId: String = "",
    val unreadMessageCount: Int = 0,
    val createdAt: Long? = null,
    val lastMessageContent: String = "",
    val lastSenderId: String = "",
    val lastMessageTime: Long? = null,
    val lastTimeSyncs: Long? = null,
    @ColumnInfo(name = "lastMessageType", defaultValue = "TEXT")
    val lastMessageType: String = "TEXT",
    @ColumnInfo(name = "ownerNick", defaultValue = "")
    val ownerNick: String = "",
    @ColumnInfo(name = "otherUserNick", defaultValue = "")
    val otherUserNick: String = "",
    //Add field
    @ColumnInfo(name = "muted", defaultValue = "false")
    val muted: Boolean = false,
    @ColumnInfo(name = "pinned", defaultValue = "false")
    val pinned: Boolean = false,
    @ColumnInfo(name = "blocked", defaultValue = "false")
    val blocked: Boolean = false,
    @ColumnInfo(name = "deleted", defaultValue = "false")
    val deleted: Boolean = false,
    //Add sync
    @ColumnInfo(name = "synced", defaultValue = "false")
    val synced: Boolean = false,
    @ColumnInfo(name = "otherUserImg", defaultValue = "")
    val otherUserImg: String = "",
    @ColumnInfo(name = "ownerImg", defaultValue = "")
    val ownerImg: String = "",
)

data class ConversationAndUser(
    @Embedded
    val conversation: ConversationEntity,

    @Embedded(prefix = "owner_")
    val owner: ConversationUserEntity,

    @Embedded(prefix = "other_")
    val otherUser: ConversationUserEntity
)