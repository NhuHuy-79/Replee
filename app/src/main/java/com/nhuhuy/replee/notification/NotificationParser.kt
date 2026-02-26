package com.nhuhuy.replee.notification

import com.google.firebase.messaging.RemoteMessage
import com.nhuhuy.replee.core.network.network.mapper.RequestMapper
import timber.log.Timber
import javax.inject.Inject

class NotificationParser @Inject constructor(
    private val requestMapper: RequestMapper,
){
    fun getNotificationBody(remoteMessage: RemoteMessage): ConversationNotificationResponse? {
        val data = remoteMessage.data
        val json = data["payload"] ?: run {
            Timber.e("No payload found!")
            return null
        }
        val networkMessage = requestMapper.parseToNetworkMessage(json)
        return ConversationNotificationResponse(
            imgUrl = networkMessage.imgUrl,
            conversationId = networkMessage.conversationId,
            senderId = networkMessage.senderId,
            receiverId = networkMessage.receiverId,
            senderName = networkMessage.senderName,
            message = networkMessage.content
        )
    }
}