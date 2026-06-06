package com.nhuhuy.replee.core.domain.repository

import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.model.settings.NotificationMode
import com.nhuhuy.replee.core.model.settings.ThemeMode

interface ProfileActionRepository {
    suspend fun updateNotification(mode: NotificationMode)
    suspend fun updateThemeMode(mode: ThemeMode)
    suspend fun updatePassword(
        email: String,
        oldPassword: String,
        newPassword: String
    ): NetworkResult<Unit>
}
