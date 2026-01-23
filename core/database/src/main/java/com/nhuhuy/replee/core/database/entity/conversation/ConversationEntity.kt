package com.nhuhuy.replee.core.database.entity.conversation

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.nhuhuy.replee.core.database.entity.account.AccountEntity

@Entity(tableName = "conversation")
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
    val synced: Boolean = false
)

data class ConversationAndUser(
    @Embedded val conversation: ConversationEntity,

    @Relation(
        parentColumn = "ownerId",
        entityColumn = "uid"
    )
    val owner: AccountEntity?,

    @Relation(
        parentColumn = "otherUserId",
        entityColumn = "uid"
    )
    val otherUser: AccountEntity?,
)


