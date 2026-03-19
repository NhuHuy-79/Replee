package com.nhuhuy.replee.core.database.entity.file_path

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "file_path"
)
data class FilePathEntity(
    @ColumnInfo("messageId", defaultValue = "")
    val messageId: String,
    @ColumnInfo("localPath", defaultValue = "")
    val localPath: String,
)
