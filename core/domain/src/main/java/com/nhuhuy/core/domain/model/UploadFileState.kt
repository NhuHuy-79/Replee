package com.nhuhuy.core.domain.model

sealed interface UploadFileState {
    data object Loading : UploadFileState
    data class Progress(val progress: Float) : UploadFileState
    data class Success(val url: String) : UploadFileState
    data class Failure(val throwable: Throwable) : UploadFileState
}

class FileUploadException(msg: String) : Exception(msg)
