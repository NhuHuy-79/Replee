package com.nhuhuy.replee.core.database.entity.file_path

import androidx.room.Embedded
import androidx.room.Relation
import com.nhuhuy.replee.core.database.entity.message.MessageEntity

data class MessageWithLocalPath(
    @Embedded
    val message: MessageEntity,

    @Relation(
        parentColumn = "messageId",
        entityColumn = "messageId"
    )
    val localFile: FilePathEntity?
)
