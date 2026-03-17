package com.nhuhuy.replee.notification

import android.app.Notification
import android.app.PendingIntent
import android.app.Person
import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import coil3.BitmapImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import com.nhuhuy.replee.R
import com.nhuhuy.replee.broadcast.ReplyBroadcast
import com.nhuhuy.replee.core.network.api.fcm.NotificationResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

abstract class NotificationFactory() {
    abstract suspend fun execute(response: NotificationResponse): Notification

    abstract fun showResult(success: Boolean): Notification

    suspend fun loadBitmapForNotification(
        context: Context,
        url: String?
    ): Bitmap? {
        return withContext(Dispatchers.IO) {
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false)
                .size(128, 128)
                .build()

            val result = context.imageLoader.execute(request)

            if (result is SuccessResult) {
                val image = result.image

                return@withContext when (image) {
                    is BitmapImage -> image.bitmap
                    else -> null
                }
            }

            null
        }
    }
}

const val REMOTE_INPUT_KEY = "conversation_key"
const val EXTRA_SENDER_ID = "sender_id"
const val EXTRA_RECEIVER_ID = "receiver_id"
const val EXTRA_CONVERSATION_ID = "conversation_id"
const val EXTRA_NOTIFICATION_ID = "notification_id"

class ConversationNotificationFactory @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationFactory() {
    override suspend fun execute(response: NotificationResponse): Notification {
        val channelId = context.getString(R.string.notification_channel)

        val bitmap = loadBitmapForNotification(
            context = context,
            url = response.senderImg
        )

        val person = Person.Builder()
            .setName(response.senderName)
            .setIcon(
                bitmap?.let { Icon.createWithBitmap(it) }
            )
            .build()

        val messagingStyle = Notification.MessagingStyle(person)
            .setConversationTitle(response.senderName)
            .addMessage(
                response.content,
                System.currentTimeMillis(),
                person
            )

        val remoteInput = RemoteInput.Builder(REMOTE_INPUT_KEY)
            .setLabel(context.getString(R.string.action_reply))
            .build()

        val replyIntent = Intent(context, ReplyBroadcast::class.java).apply {
            putExtra(EXTRA_SENDER_ID, "")
            putExtra(EXTRA_RECEIVER_ID, " ")
            putExtra(EXTRA_CONVERSATION_ID, response.conversationId)
            putExtra(EXTRA_NOTIFICATION_ID, response.hashCode())
        }

        val requestCode = response.hashCode()

        val replyPendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            replyIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val replyAction = Notification.Action.Builder(
            R.drawable.ic_notification_msg,
            context.getString(R.string.action_reply),
            replyPendingIntent
        )
            .addRemoteInput(remoteInput)
            .build()

        return Notification.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification_msg)
            .setStyle(messagingStyle)
            .setAutoCancel(true)
            .setGroup(response.conversationId)
            .addAction(replyAction)
            .build()
    }

    override fun showResult(success: Boolean): Notification {
        val title = context.getString(R.string.app_name)
        val contentText = if (success) context.getString(R.string.notification_success)
        else context.getString(R.string.notification_failure)

        return Notification.Builder(context, context.getString(R.string.notification_channel))
            .setSmallIcon(R.drawable.ic_notification_msg)
            .setContentTitle(title)
            .setContentText(contentText)
            .setAutoCancel(true)
            .build()
    }

}