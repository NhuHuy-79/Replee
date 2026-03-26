package com.nhuhuy.replee.feature_profile.data.source

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

interface ProfileNetworkDataSource {
    suspend fun updatePassword(email: String, password: String, newPassword: String)
}

class ProfileNetworkDataSourceImp @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : ProfileNetworkDataSource {

    override suspend fun updatePassword(
        email: String,
        password: String,
        newPassword: String
    ) {
        val credential = EmailAuthProvider.getCredential(email, password)
        firebaseAuth.currentUser?.reauthenticate(credential)
        firebaseAuth.currentUser?.updatePassword(newPassword)
    }

}