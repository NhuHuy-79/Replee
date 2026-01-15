package com.nhuhuy.replee.notification

import com.google.firebase.messaging.RemoteMessage
import com.nhuhuy.replee.core.firebase.network.mapper.NetworkMapper
import timber.log.Timber
import javax.inject.Inject

class NotificationParser @Inject constructor(
    private val networkMapper: NetworkMapper,
){
    fun getNotificationBody(remoteMessage: RemoteMessage) : NotificationBody? {
        val data = remoteMessage.data
        val json = data["payload"] ?: run {
            Timber.e("No payload found!")
            return null
        }
        val networkMessage = networkMapper.parseToNetworkMessage(json)
        return NotificationBody(
            conversationId = networkMessage.conversationId,
            senderId = networkMessage.senderId,
            receiverId = networkMessage.receiverId,
            senderName = networkMessage.senderName,
            message = networkMessage.content
        )
    }
}