package com.nhuhuy.core.domain.model


data class FilePath(
    val messageId: String? = null,
    val userId: String? = null,
    val localPath: String,
    val width: Int,
    val height: Int,
    val fileType: String,
    val fileSize: Long,
    val createdAt: Long = System.currentTimeMillis()
)


