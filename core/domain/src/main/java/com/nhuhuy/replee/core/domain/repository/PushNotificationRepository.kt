package com.nhuhuy.replee.core.domain.repository

import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.error_handling.NetworkResult

interface PushNotificationRepository {
    suspend fun getCurrentToken(): NetworkResult<String>
    suspend fun pushNotification(
        message: Message
    ): NetworkResult<Unit>
}
