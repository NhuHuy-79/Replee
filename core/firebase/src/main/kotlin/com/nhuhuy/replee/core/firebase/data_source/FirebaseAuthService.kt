package com.nhuhuy.replee.core.firebase.data_source

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthService @Inject constructor(
    private val auth: FirebaseAuth
) {
    class CurrentUserNotFound(msg : String = "Firebase User not found") : Exception(msg)

    fun provideCurrentUser() = auth.currentUser ?: throw CurrentUserNotFound()

    fun isUserLogged() = auth.currentUser != null

    suspend fun loginWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signUpWithEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun sendRecoverPasswordEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    suspend fun searchUserWithEmail(query: String){

    }

    suspend fun updateNewPassword(old: String, new: String){
        val user = provideCurrentUser()
        val email = user.email ?: throw IllegalStateException("Missing email")
        val credential = EmailAuthProvider.getCredential(email, old)
        user.reauthenticate(credential).await()
        user.updatePassword(new).await()
    }

    suspend fun deleteCurrentUser(){
       auth.currentUser?.delete()?.await()
    }

    fun logOut() = auth.signOut()

}