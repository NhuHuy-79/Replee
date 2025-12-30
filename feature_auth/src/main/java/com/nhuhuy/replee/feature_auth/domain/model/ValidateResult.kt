package com.nhuhuy.replee.feature_auth.domain.model

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
    }
}