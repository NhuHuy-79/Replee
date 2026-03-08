package com.nhuhuy.replee.service

import android.content.Context
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.nhuhuy.replee.core.common.data.data_store.AppDataStore
import com.nhuhuy.replee.core.common.data.data_store.NotificationMode
import com.nhuhuy.replee.notification.ConversationNotificationResponse
import com.nhuhuy.replee.notification.NotificationFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

interface PushNotificationHandler {
    suspend fun showConversationNotification(body: ConversationNotificationResponse)
}

class PushNotificationHandlerImp @Inject constructor(
    private val appDataStore: AppDataStore,
    private val notificationFactory: NotificationFactory,
    @ApplicationContext private val context: Context,
) : PushNotificationHandler{
    override suspend fun showConversationNotification(body: ConversationNotificationResponse) {
        val notificationMode = appDataStore.observeNotification().first()

        if (notificationMode == NotificationMode.NONE){
            Timber.d("User turn off notification!")
            return
        }

        val notification = notificationFactory.execute(body)
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED){
            NotificationManagerCompat.from(context)
                .notify(body.hashCode(), notification)
        } else {
            Timber.e("Permission not granted")
        }
    }
}