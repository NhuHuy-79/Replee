package com.nhuhuy.replee.core.common.data.data_source

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface SessionDataSource {
    suspend fun provideAuthenticationId(): String
}

class SessionDataSourceImp @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : SessionDataSource {
    override suspend fun provideAuthenticationId(): String {
        return firebaseAuth.currentUser?.getIdToken(true)?.await()?.token
            ?: throw Exception("Authentication id not found")
    }
}