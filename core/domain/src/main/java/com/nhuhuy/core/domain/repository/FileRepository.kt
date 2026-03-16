package com.nhuhuy.core.domain.repository

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.UploadFileState
import kotlinx.coroutines.flow.Flow

sealed interface ValidateFileResult {
    data object Valid : ValidateFileResult
    data object FileTooLarge : ValidateFileResult
}

interface FileRepository {
    suspend fun validateFileSize(uriPath: String): ValidateFileResult
    suspend fun uploadFile(uriPath: String): NetworkResult<String>
    suspend fun scheduleUploadFile(messageId: String, uriPath: String)
    fun observeUploadFile(messageId: String, uriPath: String): Flow<UploadFileState>
}