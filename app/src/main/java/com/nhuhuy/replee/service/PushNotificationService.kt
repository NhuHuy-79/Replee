package com.nhuhuy.replee.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nhuhuy.core.domain.usecase.UpdateDeviceTokenUseCase
import com.nhuhuy.replee.notification.NotificationParser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PushNotificationService() : FirebaseMessagingService() {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Inject
    lateinit var updateDeviceTokenUseCase: UpdateDeviceTokenUseCase

    @Inject
    lateinit var pushNotificationHandler: PushNotificationHandler

    @Inject
    lateinit var notificationParser: NotificationParser

    override fun onNewToken(token: String) {
        scope.launch {
            updateDeviceTokenUseCase(token)
        }
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        scope.launch {
            val notificationBody = notificationParser.getNotificationBody(message)

            if (notificationBody == null) {
                Timber.d("Notification body is null")
                return@launch
            }

            pushNotificationHandler.showConversationNotification(notificationBody)
        }
        super.onMessageReceived(message)
    }

    override fun onDestroy() {
        scope.cancel()
        Timber.d("Fcm Service is destroyed")
        super.onDestroy()
    }
}