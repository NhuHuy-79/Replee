package com.nhuhuy.replee.core.common.utils

sealed interface ValidateResult {
    data object Empty: ValidateResult
    data object Idle: ValidateResult
    data object Valid: ValidateResult
    enum class EmailError : ValidateResult{
        INVALID,
    }
    enum class PasswordError : ValidateResult{
        INVALID,
        NOT_MATCH,
        SAME_AS_OLD,
    }
}