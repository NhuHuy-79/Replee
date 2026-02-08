package com.nhuhuy.replee.feature_auth.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.nhuhuy.replee.feature_auth.R
import com.nhuhuy.replee.feature_auth.data.GoogleCredentialResult
import timber.log.Timber

class GoogleCredentialProvider {
    suspend fun provideIdToken(context: Context): GoogleCredentialResult {
        return try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.google_client_id))
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val googleIdTokenCredential = GoogleIdTokenCredential
                .createFrom(result.credential.data)

            GoogleCredentialResult.Success(googleIdTokenCredential.idToken)

        } catch (e: GetCredentialCancellationException) {
            GoogleCredentialResult.Cancelled

        } catch (e: NoCredentialException) {
            GoogleCredentialResult.NoCredential

        } catch (e: GetCredentialProviderConfigurationException) {
            GoogleCredentialResult.ProviderUnavailable

        } catch (e: GoogleIdTokenParsingException) {
            GoogleCredentialResult.InvalidCredential

        } catch (e: Exception) {
            Timber.e(e)
            GoogleCredentialResult.UnknownError
        }
    }
}