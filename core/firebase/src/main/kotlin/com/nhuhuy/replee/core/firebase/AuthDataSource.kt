package com.nhuhuy.replee.core.firebase

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthDataSource @Inject constructor(
    private val auth: FirebaseAuth
) {
    class CurrentUserNotFound(msg : String = "Firebase User not found") : Exception(msg)

    val currentUser get() = auth.currentUser ?: throw CurrentUserNotFound()

    fun provideCurrentUser() = auth.currentUser ?: throw CurrentUserNotFound()

    suspend fun loginWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signUpWithEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun sendRecoverPasswordEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    suspend fun deleteCurrentUser(){
       auth.currentUser?.delete()?.await()
    }

}