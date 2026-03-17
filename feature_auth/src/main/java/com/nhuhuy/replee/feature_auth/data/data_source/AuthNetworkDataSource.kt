package com.nhuhuy.replee.feature_auth.data.data_source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nhuhuy.core.domain.model.AuthServiceProvider
import com.nhuhuy.core.domain.model.AuthenticatedState
import com.nhuhuy.replee.core.network.model.AccountDTO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface AuthNetworkDataSource {
    suspend fun getCurrentAuthToken(): String
    suspend fun getCurrentUid(): String
    suspend fun signInWithEmail(email: String, password: String): String
    suspend fun signInWithGoogle(idToken: String): AccountDTO
    suspend fun signUpWithEmail(name: String, email: String, password: String): AccountDTO
    suspend fun sendRecoverPasswordEmail(email: String)
    fun observeAuthenticatedState(): Flow<AuthenticatedState>
}

class AuthNetworkDataSourceImp @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthNetworkDataSource {
    override suspend fun getCurrentAuthToken(): String {
        return firebaseAuth.currentUser?.getIdToken(true)?.await()?.token
            ?: throw IllegalStateException("Token not found!")
    }

    override suspend fun getCurrentUid(): String {
        return firebaseAuth.uid ?: throw IllegalStateException("Uid not found!")
    }

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): String {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password)
            .await()
        val user = result.user
        return user?.uid ?: throw IllegalStateException("User Id not found!")
    }

    override suspend fun signInWithGoogle(idToken: String): AccountDTO {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = firebaseAuth.signInWithCredential(credential).await()
        val user = authResult?.user
        return if (user != null) {
            AccountDTO(
                id = user.uid,
                email = user.email.orEmpty(),
                name = user.displayName.orEmpty(),
                imageUrl = user.photoUrl?.toString().orEmpty(),
                provider = AuthServiceProvider.GOOGLE
            )
        } else {
            throw IllegalStateException("User is null")
        }
    }

    override suspend fun signUpWithEmail(
        name: String,
        email: String,
        password: String
    ): AccountDTO {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user

        if (user != null) {
            return AccountDTO(
                name = name,
                id = user.uid,
                email = email,
                provider = AuthServiceProvider.EMAIL
            )
        } else {
            throw IllegalStateException("User not found!")
        }
    }

    override suspend fun sendRecoverPasswordEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
    }

    override fun observeAuthenticatedState(): Flow<AuthenticatedState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user == null) {
                trySend(AuthenticatedState.Unauthenticated)
            } else {
                trySend(AuthenticatedState.Authenticated(user.uid))
            }
        }

        firebaseAuth.addAuthStateListener(listener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

}