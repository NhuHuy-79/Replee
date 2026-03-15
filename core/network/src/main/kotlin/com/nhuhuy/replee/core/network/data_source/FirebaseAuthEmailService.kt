package com.nhuhuy.replee.core.network.data_source

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.nhuhuy.core.domain.model.AuthenticatedState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseAuthEmailService @Inject constructor(
    private val auth: FirebaseAuth,
    private val messaging: FirebaseMessaging
) {
    fun getCurrentUser() = auth.currentUser

    suspend fun getDeviceToken() : String = messaging.token.await()

    suspend fun loginWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signUpWithEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun sendRecoverPasswordEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    suspend fun updateNewPassword(old: String, new: String){
        val user = getCurrentUser()
        val email = user?.email ?: return
        val credential = EmailAuthProvider.getCredential(email, old)
        user.reauthenticate(credential).await()
        user.updatePassword(new).await()
    }

    suspend fun deleteCurrentUser(){
       auth.currentUser?.delete()?.await()
    }

    fun observeAuthState(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }

        auth.addAuthStateListener(listener)

        trySend(auth.currentUser?.uid)

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    fun authState(): Flow<AuthenticatedState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user == null) {
                trySend(AuthenticatedState.Unauthenticated)
            } else {
                trySend(AuthenticatedState.Authenticated(user.uid))
            }
        }

        auth.addAuthStateListener(listener)

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    fun logOut() = auth.signOut()

}