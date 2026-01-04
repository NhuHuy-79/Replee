package com.nhuhuy.replee.core.firebase.utils

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.io.IOException

fun Exception.toRemoteFailure(): RemoteFailure {
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

fun Exception.toLocalFailure() : LocalFailure {
    return when (this) {
        is IOException -> LocalFailure.IO
        else -> LocalFailure.Unknown
    }
}
