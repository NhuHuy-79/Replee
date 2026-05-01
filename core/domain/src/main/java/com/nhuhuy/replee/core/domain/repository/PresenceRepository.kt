package com.nhuhuy.replee.core.domain.repository

import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import kotlinx.coroutines.flow.Flow

interface PresenceRepository {
    suspend fun setOnline(uid: String): NetworkResult<String>
    suspend fun setOffline(uid: String): NetworkResult<String>
    fun observeOnlineState(uid: String): Flow<Boolean>
}
