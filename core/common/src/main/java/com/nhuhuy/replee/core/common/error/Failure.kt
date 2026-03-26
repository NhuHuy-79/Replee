package com.nhuhuy.replee.core.common.error

sealed interface Failure

sealed interface LocalFailure: Failure {
    data object Unknown : LocalFailure
    data object IO: LocalFailure
}

sealed interface GoogleCredentialError : RemoteFailure {
    data object Cancelled : GoogleCredentialError
    data object NoCredential : GoogleCredentialError
    data object ProviderUnavailable : GoogleCredentialError
    data object InvalidCredential : GoogleCredentialError
    data class Unknown(val message: String? = null) : GoogleCredentialError
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