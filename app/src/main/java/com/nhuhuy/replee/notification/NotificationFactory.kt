package com.nhuhuy.replee.notification

import android.app.Notification
import android.app.PendingIntent
import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import com.nhuhuy.replee.R
import com.nhuhuy.replee.deeplink.DOMAIN_URI
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import com.nhuhuy.replee.receiver.ReplyReceiver
import kotlin.jvm.java

interface NotificationFactory {
    fun execute(notificationBody: NotificationBody): Notification

    fun showResult(success: Boolean) : Notification
}

const val REMOTE_INPUT_KEY = "conversation_key"
const val EXTRA_SENDER_ID = "sender_id"
const val EXTRA_RECEIVER_ID = "receiver_id"
const val EXTRA_CONVERSATION_ID = "conversation_id"
const val EXTRA_NOTIFICATION_ID = "notification_id"

class ConversationNotificationFactory @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationFactory {
    override fun execute(notificationBody: NotificationBody): Notification {
        val uri = "$DOMAIN_URI/${notificationBody.conversationId}?senderId=${notificationBody.senderId}&receiverId=${notificationBody.receiverId}"
        /*val intent = Intent(ACTION_VIEW, uri.toUri() ).apply {

        }

        val activityIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(activityIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }*/

        val remoteInput = RemoteInput.Builder(REMOTE_INPUT_KEY)
            .setLabel(context.getString(R.string.action_reply))
            .build()

        val replyIntent = Intent(context, ReplyReceiver::class.java).apply {
            putExtra(EXTRA_SENDER_ID, notificationBody.receiverId)
            putExtra(EXTRA_RECEIVER_ID, notificationBody.senderId)
            putExtra(EXTRA_CONVERSATION_ID, notificationBody.conversationId)
            putExtra(EXTRA_NOTIFICATION_ID, notificationBody.hashCode())
        }
        val replyPendingIntent = PendingIntent.getBroadcast(context, 0, replyIntent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val replyAction = Notification.Action.Builder(
            R.drawable.ic_notification_msg,
            context.getString(R.string.action_reply),
            replyPendingIntent
        ).addRemoteInput(remoteInput)
            .build()

        return Notification.Builder(context, context.getString(R.string.notification_channel))
            .setSmallIcon(R.drawable.ic_notification_msg)
            .setContentTitle(notificationBody.senderName)
            .setContentText(notificationBody.message)
            .setAutoCancel(true)
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