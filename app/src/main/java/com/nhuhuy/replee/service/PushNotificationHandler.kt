package com.nhuhuy.replee.service

import android.content.Context
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.nhuhuy.replee.notification.NotificationBody
import com.nhuhuy.replee.notification.NotificationFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

interface PushNotificationHandler {
    fun showConversationNotification(body: NotificationBody)
}

class PushNotificationHandlerImp @Inject constructor(
    private val notificationFactory: NotificationFactory,
    @ApplicationContext private val context: Context,
) : PushNotificationHandler{
    override fun showConversationNotification(body: NotificationBody) {
        val notification = notificationFactory.execute(body)
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED){
            NotificationManagerCompat.from(context)
                .notify(body.hashCode(), notification)
        } else {
            Timber.e("Permission not granted")
        }
    }
}