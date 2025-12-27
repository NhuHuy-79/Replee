package com.nhuhuy.replee.core.firebase.mapper

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure

fun Exception.toAuthError(): RemoteFailure {
    return when (this) {
        is FirebaseAuthInvalidUserException -> RemoteFailure.Auth.USER_NOT_FOUND
        is FirebaseAuthWeakPasswordException -> RemoteFailure.Auth.WEAK_PASSWORD
        is FirebaseAuthInvalidCredentialsException -> RemoteFailure.Auth.INVALID_CREDENTIAL
        is FirebaseAuthEmailException -> RemoteFailure.Auth.CANNOT_SEND_EMAIL
        is FirebaseAuthUserCollisionException -> RemoteFailure.Auth.USER_ALREADY_EXIST
        is FirebaseNetworkException -> RemoteFailure.Network
        else -> RemoteFailure.Unknown
    }
}
