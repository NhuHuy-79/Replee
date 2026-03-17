package com.nhuhuy.replee.broadcast

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.feature_chat.domain.usecase.message.SendMessageUseCase
import com.nhuhuy.replee.notification.ConversationNotificationFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

interface ReplyManager {
    suspend fun handleReplyMessage(
        conversationId: String,
        senderId: String,
        receiverId: String,
        message: String
    ): NetworkResult<String>

    fun showNotification(success: Boolean)
}

class ReplyManagerImp @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val notificationFactory: ConversationNotificationFactory,
    @ApplicationContext private val context: Context
) : ReplyManager {
    override suspend fun handleReplyMessage(
        conversationId: String,
        senderId: String,
        receiverId: String,
        message: String
    ): NetworkResult<String> {
        return sendMessageUseCase(
            conversationId = conversationId,
            senderId = senderId,
            receiverId = receiverId,
            text = message
        )
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