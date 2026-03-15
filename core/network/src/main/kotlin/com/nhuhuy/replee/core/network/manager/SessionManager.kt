package com.nhuhuy.replee.core.network.manager

import com.google.firebase.auth.FirebaseAuth
import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.model.AuthenticatedState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SessionManagerImp @Inject constructor(
    private val auth: FirebaseAuth,
) : SessionManager {

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
        }

    override val authState: Flow<String?>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                trySend(auth.currentUser?.uid)
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    override fun requireUserId(): String {
        return requireNotNull(auth.currentUser?.uid) { "Error: User is not logged in" }
    }

    override fun getUserIdOrNull(): String? {
        return auth.currentUser?.uid
    }

    override fun logout() {
        auth.signOut()
    }


}