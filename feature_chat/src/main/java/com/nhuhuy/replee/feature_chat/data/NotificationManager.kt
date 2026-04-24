package com.nhuhuy.replee.feature_chat.data

interface NotificationManager {
    fun cancelNotification(notificationId: Int)

    fun cancelNotificationList(notificationIds: List<Int>)

    fun removeShortcut(shortcutIds: List<String>)
}