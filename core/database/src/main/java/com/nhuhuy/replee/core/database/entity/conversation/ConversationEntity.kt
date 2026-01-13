package com.nhuhuy.replee.core.database.entity.conversation

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


