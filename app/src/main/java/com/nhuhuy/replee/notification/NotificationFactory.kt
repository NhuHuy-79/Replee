package com.nhuhuy.replee.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.LocusIdCompat
import androidx.core.graphics.drawable.IconCompat
import coil3.BitmapImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
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
                .size(256, 256)
                .transformations(CircleCropTransformation())
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
        val bitmap = loadBitmapForNotification(context, response.senderImg)

        val sender = Person.Builder()
            .setName(response.senderName)
            .setIcon(bitmap?.let { IconCompat.createWithBitmap(it) })
            .setKey(response.senderId)
            .setImportant(true)
            .build()

        val user = Person.Builder()
            .setName(context.getString(R.string.app_name))
            .setKey(response.receiverId)
            .build()

        val notificationMessage = NotificationCompat.MessagingStyle.Message(
            response.content,
            System.currentTimeMillis(),
            sender
        )
        val messagingStyle = NotificationCompat.MessagingStyle(user)
            .setConversationTitle(response.senderName)
            .addMessage(notificationMessage)

        val contentIntent =
            context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
                putExtra(EXTRA_CONVERSATION_ID, response.conversationId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            response.conversationId.hashCode(),
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val remoteInput = RemoteInput.Builder(REMOTE_INPUT_KEY)
            .setLabel(context.getString(R.string.action_reply))
            .build()

        val replyIntent = Intent(context, ReplyBroadcast::class.java).apply {
            putExtra(EXTRA_CONVERSATION_ID, response.conversationId)
            putExtra(EXTRA_SENDER_ID, response.receiverId)
            putExtra(EXTRA_RECEIVER_ID, response.senderId)
            putExtra(EXTRA_NOTIFICATION_ID, response.hashCode())
        }

        val replyPendingIntent = PendingIntent.getBroadcast(
            context,
            response.conversationId.hashCode(),
            replyIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val replyAction = NotificationCompat.Action.Builder(
            R.drawable.ic_circle_notification,
            context.getString(R.string.action_reply),
            replyPendingIntent
        )
            .addRemoteInput(remoteInput)
            .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_REPLY)
            .build()

        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_circle_notification)
            .setStyle(messagingStyle)
            .setContentIntent(contentPendingIntent)
            .setGroup(response.conversationId)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
            .addAction(replyAction)
            .setShortcutId(response.conversationId)
            .setLocusId(LocusIdCompat(response.conversationId))
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .build()
    }

    override fun showResult(success: Boolean): Notification {
        val title = context.getString(R.string.app_name)
        val contentText = if (success) context.getString(R.string.notification_success)
        else context.getString(R.string.notification_failure)

        return NotificationCompat.Builder(context, context.getString(R.string.notification_channel))
            .setSmallIcon(R.drawable.ic_notification_msg)
            .setContentTitle(title)
            .setContentText(contentText)
            .setAutoCancel(true)
            .build()
    }
}
