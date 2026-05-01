package com.nhuhuy.replee.core.model.chat

data class LocalPathMessage(
    val message: Message,
    val localPath: String?,
    val width: Int = 0,
    val height: Int = 0,
)
