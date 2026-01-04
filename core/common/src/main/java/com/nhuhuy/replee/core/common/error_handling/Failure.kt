package com.nhuhuy.replee.core.common.error_handling

sealed interface Failure

sealed interface LocalFailure: Failure {
    data object Unknown : LocalFailure
    data object IO: LocalFailure
}

sealed interface RemoteFailure : Failure{
    data object Network: RemoteFailure

    enum class Auth : RemoteFailure{
        WEAK_PASSWORD,
        CANNOT_SEND_EMAIL,
        USER_NOT_FOUND,
        USER_ALREADY_EXIST,
        INVALID_CREDENTIAL
    }

    data object Unknown: RemoteFailure
}