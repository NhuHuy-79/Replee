package com.nhuhuy.replee.core.common.data.data_store

import kotlinx.coroutines.flow.Flow

interface AppDataStore {
    suspend fun saveNotificationMode(mode: NotificationMode)
    suspend fun saveThemeMode(mode: ThemeMode)
    fun observeNotification(): Flow<NotificationMode>
    fun observeTheme(): Flow<ThemeMode>
    suspend fun getAuthenticationToken(): String?
    suspend fun saveAuthenticationToken(token: String)
}