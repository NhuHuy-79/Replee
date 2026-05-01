package com.nhuhuy.replee.core.model

sealed interface ValidateFileResult {
    data object Valid : ValidateFileResult
    data object FileTooLarge : ValidateFileResult
    data object UnSupported : ValidateFileResult
    data object Unknown : ValidateFileResult
}
