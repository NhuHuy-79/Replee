package com.nhuhuy.replee.core.database.entity.file_path

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nhuhuy.replee.core.database.entity.account.AccountEntity
import com.nhuhuy.replee.core.database.entity.message.MessageEntity

@Entity(
    tableName = "file_path",
    foreignKeys = [
        ForeignKey(
            entity = MessageEntity::class,
            parentColumns = ["messageId"],
            childColumns = ["messageId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["uid"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["messageId"]),
        Index(value = ["userId"])
    ]
)
data class FilePathEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val messageId: String? = null,
    val userId: String? = null,
    val localPath: String,
    val width: Int,
    val height: Int,
    val fileType: String,
    val fileSize: Long,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
