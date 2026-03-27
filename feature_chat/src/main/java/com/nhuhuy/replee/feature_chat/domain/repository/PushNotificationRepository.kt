package com.nhuhuy.replee.feature_chat.domain.repository

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.model.message.Message

interface PushNotificationRepository {
    suspend fun getCurrentToken(): NetworkResult<String>
    suspend fun pushNotification(
        message: Message
    ): NetworkResult<Unit>
}