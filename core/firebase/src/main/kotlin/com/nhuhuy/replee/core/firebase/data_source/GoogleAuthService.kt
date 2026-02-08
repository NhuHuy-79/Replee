package com.nhuhuy.replee.core.firebase.data_source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nhuhuy.core.domain.model.AuthServiceProvider
import com.nhuhuy.replee.core.firebase.data.AccountDTO
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleAuthService @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun signIn(idToken: String): AccountDTO {
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
}