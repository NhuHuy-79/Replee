package com.nhuhuy.replee.feature_chat.domain.model.message

import com.nhuhuy.replee.core.database.entity.file_path.MessageWithLocalPath
import com.nhuhuy.replee.feature_chat.data.mapper.toMessage

data class LocalPathMessage(
    val message: Message,
    val localPath: String?,
    val width: Int = 0,
    val height: Int = 0,
)

fun MessageWithLocalPath.toLocalPathMessage(): LocalPathMessage {
    return LocalPathMessage(
        message = message.toMessage(),
        localPath = localFile?.localPath,
        height = localFile?.height ?: 0,
        width = localFile?.width ?: 0
    )
}

