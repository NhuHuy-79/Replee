package com.nhuhuy.core.domain.repository

import com.nhuhuy.core.domain.model.NetworkResult
import kotlinx.coroutines.flow.Flow

interface PresenceRepository {
    suspend fun setOnline(uid: String): NetworkResult<String>
    suspend fun setOffline(uid: String): NetworkResult<String>
    fun observeOnlineState(uid: String): Flow<Boolean>
}