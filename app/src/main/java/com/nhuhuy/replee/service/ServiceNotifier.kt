package com.nhuhuy.replee.service

import android.content.Context
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.nhuhuy.replee.core.database.data_store.AppDataStore
import com.nhuhuy.replee.core.model.NotificationMode
import com.nhuhuy.replee.core.network.api.fcm.NotificationResponse
import com.nhuhuy.replee.notification.NotificationFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

interface ServiceNotifier {
    suspend fun showConversationNotification(body: NotificationResponse)
}

class ServiceNotifierImp @Inject constructor(
    private val appDataStore: AppDataStore,
    private val notificationFactory: NotificationFactory,
    @ApplicationContext private val context: Context,
) : ServiceNotifier {
    override suspend fun showConversationNotification(body: NotificationResponse) {
        val notificationMode = appDataStore.observeNotification().first()
        if (notificationMode == NotificationMode.NONE){
            Timber.d("User turn off notification!")
            return
        }

        val notification = notificationFactory.execute(body)
        val summaryNotification = notificationFactory.createSummaryNotification(body)

        val childId = body.messageId.hashCode()
        val summaryId = body.conversationId.hashCode()

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED){
            val manager = NotificationManagerCompat.from(context)
            manager.notify(childId, notification)
            manager.notify(summaryId, summaryNotification)
        } else {
            Timber.e("Permission not granted")
        }
    }
}
