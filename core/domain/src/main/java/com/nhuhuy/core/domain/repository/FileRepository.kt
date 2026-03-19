package com.nhuhuy.core.domain.repository

import com.nhuhuy.core.domain.model.FilePath
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.UploadFileState
import com.nhuhuy.core.domain.model.ValidateFileResult

import kotlinx.coroutines.flow.Flow


interface FileRepository {
    suspend fun getUriPathWithMessageId(messageId: String): FilePath?
    suspend fun upsertFilePath(filePath: FilePath)
    suspend fun saveFileToInternalStorage(uriPath: String): String
    suspend fun validateFileSize(uriPath: String): ValidateFileResult
    suspend fun uploadFile(uriPath: String): NetworkResult<String>
    suspend fun scheduleUploadFile(messageId: String, uriPath: String)
    fun observeUploadFile(messageId: String, uriPath: String): Flow<UploadFileState>

    suspend fun getFileMetadata(uriPath: String): FileMetadata
}

data class FileMetadata(
    val width: Int = 0,
    val height: Int = 0,
    val size: Long = 0,
    val mimeType: String = "",
    val extension: String = ""
)