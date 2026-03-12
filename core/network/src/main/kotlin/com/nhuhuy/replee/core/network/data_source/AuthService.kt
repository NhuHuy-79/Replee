package com.nhuhuy.replee.core.network.data_source

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed interface AuthenticatedState {
    data object Loading : AuthenticatedState
    data class Authenticated(val uid: String) : AuthenticatedState
    data object Unauthenticated : AuthenticatedState
}

interface AuthService {
    fun getCurrentUser(): FirebaseUser?
    suspend fun getDeviceToken(): String
    suspend fun loginWithEmail(email: String, password: String)
    suspend fun signUpWithEmail(email: String, password: String)
    suspend fun sendRecoverPasswordEmail(email: String)
    suspend fun updateNewPassword(old: String, new: String)
    suspend fun deleteCurrentUser()
    fun observeAuthState(): Flow<String?>
    fun authState(): Flow<AuthenticatedState>
    fun logOut()
}

class AuthServiceImp @Inject constructor(
    private val auth: FirebaseAuth,
    private val messaging: FirebaseMessaging
) : AuthService {
    override fun getCurrentUser() = auth.currentUser

    override suspend fun getDeviceToken(): String = messaging.token.await()

    override suspend fun loginWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun signUpWithEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun sendRecoverPasswordEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun updateNewPassword(old: String, new: String) {
        val user = getCurrentUser()
        val email = user?.email ?: return
        val credential = EmailAuthProvider.getCredential(email, old)
        user.reauthenticate(credential).await()
        user.updatePassword(new).await()
    }

    override suspend fun deleteCurrentUser() {
        auth.currentUser?.delete()?.await()
    }

    override fun observeAuthState(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        trySend(auth.currentUser?.uid)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override fun authState(): Flow<AuthenticatedState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user == null) {
                trySend(AuthenticatedState.Unauthenticated)
            } else {
                trySend(AuthenticatedState.Authenticated(user.uid))
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override fun logOut() = auth.signOut()
}
