package com.nhuhuy.replee.receiver

import android.content.Context
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.nhuhuy.replee.core.common.error_handling.onSuccess
import com.nhuhuy.replee.feature_chat.data.SendMessageService
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import com.nhuhuy.replee.notification.ConversationNotificationFactory
import dagger.hilt.android.internal.Contexts
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

interface ReceiverHandler {
    suspend fun handleReplyMessage(conversationId: String, senderId: String, receiverId: String, message: String)

    fun showNotification(success: Boolean)
}

class ReceiverHandlerImp @Inject constructor(
    private val messageRepository: MessageRepository,
    private val sendMessageService: SendMessageService,
    private val notificationFactory: ConversationNotificationFactory,
    @ApplicationContext private val context: Context
) : ReceiverHandler{
    override suspend fun handleReplyMessage(
        conversationId: String,
        senderId: String,
        receiverId: String,
        message: String
    ) {
        val newMessage = Message(
            conversationId = conversationId,
            messageId = UUID.randomUUID().toString(),
            senderId = senderId,
            receiverId = receiverId,
            content = message,
            seen = false
        )
        messageRepository.addNewMessage(conversationId = conversationId, message = newMessage)
            .onSuccess { message ->
                sendMessageService.sendMessage(message)
            }
    }

    override fun showNotification(success: Boolean){
        val notification = notificationFactory.showResult(success)
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED){
            NotificationManagerCompat.from(context).notify(notification.hashCode(), notification)
        } else {
            Timber.e("Permission not granted")
        }
    }

}