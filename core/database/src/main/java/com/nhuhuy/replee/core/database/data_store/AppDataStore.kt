package com.nhuhuy.replee.core.database.data_store

import com.nhuhuy.replee.core.model.NotificationMode
import com.nhuhuy.replee.core.model.SeedColor
import com.nhuhuy.replee.core.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface AppDataStore {
    suspend fun saveNotificationMode(mode: NotificationMode)
    suspend fun saveThemeMode(mode: ThemeMode)
    fun observeNotification(): Flow<NotificationMode>
    fun observeTheme(): Flow<ThemeMode>
    fun getAuthenticationToken(): Flow<String>
    suspend fun saveAuthenticationToken(token: String)
    fun observeChatColor(): Flow<SeedColor>
    suspend fun saveChatColor(color: SeedColor)
}
