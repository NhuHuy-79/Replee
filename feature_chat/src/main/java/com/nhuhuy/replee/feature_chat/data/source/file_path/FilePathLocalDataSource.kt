package com.nhuhuy.replee.feature_chat.data.source.file_path

import com.nhuhuy.core.domain.model.FilePath
import com.nhuhuy.replee.core.common.data.mapper.toEntity
import com.nhuhuy.replee.core.database.entity.file_path.FilePathDao
import javax.inject.Inject

interface FilePathLocalDataSource {
    suspend fun upsertFilePath(filePath: FilePath)
    suspend fun getFilePathByMessageId(messageId: String): FilePath?
    suspend fun deleteFilePathByMessageId(messageId: String)
}


class FilePathLocalDataSourceImp @Inject constructor(
    private val filePathDao: FilePathDao
) : FilePathLocalDataSource {
    override suspend fun upsertFilePath(filePath: FilePath) {
        filePathDao.upsert(filePath.toEntity())
    }

    override suspend fun getFilePathByMessageId(messageId: String): FilePath? {
        TODO("")
    }

    override suspend fun deleteFilePathByMessageId(messageId: String) {
        TODO("Not yet implemented")
    }

}