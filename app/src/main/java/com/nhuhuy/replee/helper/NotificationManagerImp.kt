package com.nhuhuy.replee.helper

import android.content.Context
import androidx.core.content.pm.ShortcutManagerCompat
import com.nhuhuy.replee.feature_chat.data.NotificationManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationManagerImp @Inject constructor(
    @ApplicationContext private val context: Context,
) : NotificationManager {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager


    override fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    override fun cancelNotificationList(notificationIds: List<Int>) {
        notificationIds.forEach { id ->
            cancelNotification(id)
        }
    }

    override fun removeShortcut(shortcutIds: List<String>) {
        ShortcutManagerCompat.removeDynamicShortcuts(context, shortcutIds)
    }
}