package com.nhuhuy.replee.notification

import android.app.Notification
import android.content.Context
import com.nhuhuy.replee.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface NotificationFactory {
    fun execute(notificationBody: NotificationBody): Notification
}

class ConversationNotificationFactory @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationFactory {
    override fun execute(notificationBody: NotificationBody): Notification {
        return Notification.Builder(context, context.getString(R.string.notification_channel))
            .setSmallIcon(R.drawable.ic_notification_msg)
            .setContentTitle(notificationBody.senderName)
            .setContentText(notificationBody.message)
            .build()
    }

}