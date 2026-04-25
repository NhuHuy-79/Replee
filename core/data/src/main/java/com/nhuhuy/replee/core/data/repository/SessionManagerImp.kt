package com.nhuhuy.replee.core.data.repository

import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.model.AuthenticatedState
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.data.data_store.AppDataStore
import com.nhuhuy.replee.core.data.utils.IoDispatcher
import com.nhuhuy.replee.core.data.utils.execute
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class SessionManagerImp @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging,
    private val credentialManager: CredentialManager,
    private val dataStore: AppDataStore,
    private val auth: FirebaseAuth,
    private val accountLocalDataSource: AccountLocalDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : SessionManager {

    override val userIdState: Flow<String?>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                val uid = auth.uid
                trySend(uid)
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }.flowOn(ioDispatcher)

    override val authenticatedState: Flow<AuthenticatedState>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                val uid = auth.uid
                if (uid == null) {
                    trySend(AuthenticatedState.Unauthenticated)
                } else {
                    trySend(AuthenticatedState.Authenticated(uid))
                }
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }.flowOn(ioDispatcher)

    override fun requireUserId(): String {
        return requireNotNull(auth.currentUser?.uid) { "Error: User is not logged in" }
    }

    override suspend fun refreshAuthenticationToken(token: String) {
        dataStore.saveAuthenticationToken(token)
    }

    override fun getAuthenticationToken(): Flow<String> {
        return dataStore.getAuthenticationToken()
    }

    override fun getUserIdOrNull(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun getNewAuthenticatedToken(): String {
        return requireNotNull(
            auth.currentUser?.getIdToken(true)?.await()?.token
        )
    }

    override suspend fun getCurrentDeviceToken(): NetworkResult<String> {
        return execute { firebaseMessaging.token.await() }
    }

    override suspend fun logout() {
        try {
            val uid = auth.currentUser?.uid
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            uid?.let {
                accountLocalDataSource.updateLogoutStatus(it)
            }
        } catch (e: Exception) {
            Timber.e(e)
        } finally {
            dataStore.saveAuthenticationToken("")
            auth.signOut()
        }
    }
}
