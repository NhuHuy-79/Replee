package com.nhuhuy.replee.core.domain.repository

import com.nhuhuy.replee.core.model.NotificationMode
import com.nhuhuy.replee.core.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface ProfileQueryRepository {
    fun observeNotification(): Flow<NotificationMode>
    fun observeTheme(): Flow<ThemeMode>
}
