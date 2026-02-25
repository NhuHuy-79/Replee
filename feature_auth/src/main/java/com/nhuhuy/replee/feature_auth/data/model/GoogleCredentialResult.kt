package com.nhuhuy.replee.feature_auth.data.model

import androidx.annotation.StringRes
import com.nhuhuy.replee.feature_auth.R

sealed interface GoogleCredentialResult {
    data object Loading : GoogleCredentialResult

    data class Success(val idToken: String) : GoogleCredentialResult

    data object Cancelled : GoogleCredentialResult

    data object NoCredential : GoogleCredentialResult
    // No Google account / no credential found

    data object ProviderUnavailable : GoogleCredentialResult
    // Google Play services / provider unavailable

    data object InvalidCredential : GoogleCredentialResult

    data object UnknownError : GoogleCredentialResult
}

@StringRes
fun GoogleCredentialResult.toStringRes(): Int? = when (this) {
    is GoogleCredentialResult.Success -> null

    GoogleCredentialResult.Cancelled ->
        R.string.auth_google_sign_in_cancelled

    GoogleCredentialResult.NoCredential ->
        R.string.auth_google_sign_in_no_credential

    GoogleCredentialResult.ProviderUnavailable ->
        R.string.auth_google_sign_in_provider_unavailable

    GoogleCredentialResult.InvalidCredential ->
        R.string.auth_google_sign_in_invalid_credential

    is GoogleCredentialResult.UnknownError ->
        R.string.auth_google_sign_in_unknown_error
}