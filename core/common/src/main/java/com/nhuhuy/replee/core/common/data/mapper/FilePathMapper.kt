package com.nhuhuy.replee.core.common.data.mapper

import com.nhuhuy.core.domain.model.FilePath
import com.nhuhuy.replee.core.database.entity.file_path.FilePathEntity

fun FilePath.toEntity() = FilePathEntity(
    messageId = messageId,
    userId = userId,
    localPath = localPath,
    width = width,
    height = height,
    fileType = fileType,
    fileSize = fileSize,
    createdAt = createdAt
)

fun FilePathEntity.toFilePath() = FilePath(
    messageId = messageId,
    userId = userId,
    localPath = localPath,
    width = width,
    height = height,
    fileType = fileType,
    fileSize = fileSize,
    createdAt = createdAt
)
