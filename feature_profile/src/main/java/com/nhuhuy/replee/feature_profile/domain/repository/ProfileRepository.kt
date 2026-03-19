package com.nhuhuy.replee.feature_profile.domain.repository

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.data.data_store.NotificationMode
import com.nhuhuy.replee.core.common.data.data_store.ThemeMode
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun updateNotification(mode: NotificationMode)
    suspend fun updateThemeMode(mode: ThemeMode)
    fun observeNotification(): Flow<NotificationMode>
    fun observeTheme(): Flow<ThemeMode>
    suspend fun updateUserImage(uriPath: String): NetworkResult<String>
    suspend fun logOut(): String
    suspend fun updatePassword(
        email: String,
        oldPassword: String,
        newPassword: String
    ): NetworkResult<Unit>
}