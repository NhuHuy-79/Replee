package com.nhuhuy.replee.core.common

import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.GetCredentialUnsupportedException
import androidx.credentials.exceptions.NoCredentialException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.nhuhuy.replee.core.common.error_handling.GoogleCredentialError
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure

fun Exception.toRemoteFailure(): RemoteFailure {
    return when (this) {
        is FirebaseAuthException -> this.toRemoteFailure()
        is FirebaseNetworkException -> RemoteFailure.Network
        else -> RemoteFailure.Unknown
    }
}

fun Throwable.toRemoteFailure(): RemoteFailure {
    return when (this) {
        is FirebaseAuthException -> this.toRemoteFailure()
        is FirebaseNetworkException -> RemoteFailure.Network
        is GetCredentialException -> this.toRemoteFailure()
        else -> RemoteFailure.Unknown
    }
}

fun GetCredentialException.toRemoteFailure(): RemoteFailure {
    return when (this) {
        is GetCredentialCancellationException ->
            GoogleCredentialError.Cancelled

        is NoCredentialException ->
            GoogleCredentialError.NoCredential

        is GetCredentialProviderConfigurationException,
        is GetCredentialUnsupportedException ->
            GoogleCredentialError.ProviderUnavailable

        else -> GoogleCredentialError.Unknown(this.message)
    }
}

fun FirebaseAuthException.toRemoteFailure() : RemoteFailure {
    return when (this) {
        is FirebaseAuthInvalidUserException -> RemoteFailure.Auth.USER_NOT_FOUND
        is FirebaseAuthWeakPasswordException -> RemoteFailure.Auth.WEAK_PASSWORD
        is FirebaseAuthInvalidCredentialsException -> RemoteFailure.Auth.INVALID_CREDENTIAL
        is FirebaseAuthEmailException -> RemoteFailure.Auth.CANNOT_SEND_EMAIL
        is FirebaseAuthUserCollisionException -> RemoteFailure.Auth.USER_ALREADY_EXIST
        else -> RemoteFailure.Unknown
    }
}
