package com.nhuhuy.replee.feature_chat.data

interface NotificationHelper {
    fun cancelNotification(notificationId: Int)

    fun cancelNotificationList(notificationIds: List<Int>)

    fun removeShortcut(shortcutIds: List<String>)
}