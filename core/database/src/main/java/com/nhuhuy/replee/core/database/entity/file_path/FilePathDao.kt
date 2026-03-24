package com.nhuhuy.replee.core.database.entity.file_path

import androidx.room.Dao
import androidx.room.Query
import com.nhuhuy.replee.core.database.base.BaseDao

@Dao
interface FilePathDao : BaseDao<FilePathEntity> {
    @Query("SELECT * FROM file_path WHERE messageId = :messageId")
    suspend fun getLocalPathWithMessageId(messageId: String): FilePathEntity?

    @Query("SELECT * FROM file_path WHERE userId = :userId")
    fun getLocalPathWithUserId(userId: String): FilePathEntity?
}