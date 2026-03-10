package com.nhuhuy.replee.notification

import com.google.firebase.messaging.RemoteMessage
import com.nhuhuy.replee.core.network.api.mapper.RequestMapper
import com.nhuhuy.replee.core.network.api.model.NotificationResponse
import timber.log.Timber
import javax.inject.Inject

class NotificationParser @Inject constructor(
    private val requestMapper: RequestMapper,
){
    fun getNotificationBody(remoteMessage: RemoteMessage): NotificationResponse? {
        val data = remoteMessage.data
        val json = data["payload"] ?: run {
            Timber.e("No payload found!")
            return null
        }

        return requestMapper.parseToNetworkMessage(json)
    }
}