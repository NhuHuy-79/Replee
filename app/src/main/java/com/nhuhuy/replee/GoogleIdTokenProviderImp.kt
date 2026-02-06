package com.nhuhuy.replee

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.NetworkResultCaller
import com.nhuhuy.core.domain.utils.Logger
import com.nhuhuy.replee.feature_auth.data.GoogleIdTokenProvider
import kotlinx.coroutines.CoroutineDispatcher

class GoogleIdTokenProviderImp(
    logger: Logger,
    ioDispatcher: CoroutineDispatcher,
    private val context: Context
) : GoogleIdTokenProvider, NetworkResultCaller(dispatcher = ioDispatcher, logger) {
    private val credentialManager = CredentialManager.create(context)

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun getIdToken(): NetworkResult<String> {
        return safeCall {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential

            val googleIdTokenCredential = GoogleIdTokenCredential
                .createFrom(credential.data)
            googleIdTokenCredential.idToken
        }
    }

}