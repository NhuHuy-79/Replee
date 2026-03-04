package com.nhuhuy.core.domain.model

sealed interface FileState {
    data object Loading : FileState
    data class Progress(val progress: Float) : FileState
    data class Success(val url: String) : FileState
    data class Failure(val throwable: Throwable) : FileState
}

class FileUploadException(msg: String) : Exception(msg)
