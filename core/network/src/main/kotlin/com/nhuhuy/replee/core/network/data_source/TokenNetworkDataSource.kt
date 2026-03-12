package com.nhuhuy.replee.core.network.data_source

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface TokenNetworkDataSource {
    suspend fun getDeviceToken(): String
}

class TokenNetworkDataSourceImp @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging
) : TokenNetworkDataSource {
    override suspend fun getDeviceToken(): String {
        return firebaseMessaging.token.await()
    }

}