package com.nhuhuy.replee.core.database.mapper

import com.nhuhuy.replee.core.database.entity.file_path.MessageWithLocalPath
import com.nhuhuy.replee.core.model.chat.LocalPathMessage

fun MessageWithLocalPath.toLocalPathMessage(): LocalPathMessage {
    return LocalPathMessage(
        message = message.toMessage(),
        localPath = localFile?.localPath,
        height = localFile?.height ?: 0,
        width = localFile?.width ?: 0
    )
}
