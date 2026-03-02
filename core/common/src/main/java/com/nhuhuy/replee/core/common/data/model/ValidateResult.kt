package com.nhuhuy.replee.core.common.data.model

sealed interface ValidateResult {
    data object Empty : ValidateResult
    data object Idle : ValidateResult
    data object Valid : ValidateResult
    enum class EmailError : ValidateResult {
        INVALID,
    }

    enum class NameError : ValidateResult {
        TOO_LONG,
    }

    enum class PasswordError : ValidateResult {
        INVALID,
        NOT_MATCH,
        SAME_AS_OLD,
    }
}