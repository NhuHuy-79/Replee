package com.nhuhuy.replee.receiver

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.nhuhuy.replee.core.common.error_handling.onFailureSuspend
import com.nhuhuy.replee.core.common.error_handling.onSuccessSuspend
import com.nhuhuy.replee.feature_chat.data.SendMessageService
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import com.nhuhuy.replee.notification.ConversationNotificationFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

interface ReceiverHandler {
    suspend fun handleReplyMessage(conversationId: String, senderId: String, receiverId: String, message: String)

    fun showNotification(success: Boolean)
}

class ReceiverHandlerImp @Inject constructor(
    private val syncManager: SyncManager,
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
            seen = false,
            status = MessageStatus.PENDING,
        )
        messageRepository.addNewMessage(conversationId = conversationId, message = newMessage)
            .onSuccessSuspend { message ->
                syncManager.updateMessageStatusInLocal(
                    messageId = message.messageId,
                    status = MessageStatus.SYNCED
                )
                sendMessageService.sendMessage(message)
            }
            .onFailureSuspend {
                syncManager.updateMessageStatusInLocal(
                    messageId = newMessage.messageId,
                    status = MessageStatus.FAILED
                )
            }
    }

    override fun showNotification(success: Boolean){
        val notification = notificationFactory.showResult(success)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
            NotificationManagerCompat.from(context).notify(notification.hashCode(), notification)
        } else {
            Timber.e("Permission not granted")
        }
    }

}