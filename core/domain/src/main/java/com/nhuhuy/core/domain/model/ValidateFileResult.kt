package com.nhuhuy.core.domain.model

sealed interface ValidateFileResult {
    data object Valid : ValidateFileResult
    data object FileTooLarge : ValidateFileResult
    data object UnSupported : ValidateFileResult
    data object Unknown : ValidateFileResult
}


