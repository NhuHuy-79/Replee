package com.nhuhuy.replee.feature_auth.utils

import androidx.annotation.StringRes
import com.nhuhuy.replee.core.common.base.ValidateResult
import com.nhuhuy.replee.core.common.error.GoogleCredentialError
import com.nhuhuy.replee.core.common.error.RemoteFailure
import com.nhuhuy.replee.feature_auth.R

fun ValidateResult.toUiText() : Int? {
    return when (this) {
        ValidateResult.EmailError.INVALID -> R.string.invalid_email
        ValidateResult.Empty -> R.string.empty_input
        ValidateResult.PasswordError.INVALID -> R.string.invalid_password
        ValidateResult.PasswordError.NOT_MATCH -> R.string.password_not_match
        else -> null
    }
}

@StringRes
fun GoogleCredentialError.toStringRes(): Int {
    return when (this) {
        GoogleCredentialError.Cancelled ->
            R.string.auth_google_sign_in_cancelled

        GoogleCredentialError.NoCredential ->
            R.string.auth_google_sign_in_no_credential

        GoogleCredentialError.ProviderUnavailable ->
            R.string.auth_google_sign_in_provider_unavailable

        GoogleCredentialError.InvalidCredential ->
            R.string.auth_google_sign_in_invalid_credential

        is GoogleCredentialError.Unknown ->
            R.string.auth_google_sign_in_unknown_error
    }
}

fun RemoteFailure.toUiText() : Int{
    return when (this) {
        RemoteFailure.Auth.WEAK_PASSWORD -> R.string.failure_weak_password
        RemoteFailure.Auth.CANNOT_SEND_EMAIL -> R.string.failure_cannot_send_email
        RemoteFailure.Auth.USER_NOT_FOUND -> R.string.failure_user_not_found
        RemoteFailure.Auth.USER_ALREADY_EXIST -> R.string.failure_user_already_exist
        RemoteFailure.Auth.INVALID_CREDENTIAL -> R.string.failure_invalid_credential
        RemoteFailure.Network -> R.string.failure_network
        RemoteFailure.Unknown -> R.string.failure_unknown
        is GoogleCredentialError -> this.toStringRes()
    }
}