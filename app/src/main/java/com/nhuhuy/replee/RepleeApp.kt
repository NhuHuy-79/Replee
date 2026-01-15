package com.nhuhuy.replee

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class RepleeApp() : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this)
        Timber.plant(Timber.DebugTree())
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            context.getString(R.string.notification_channel),                  // ID
            "Chat Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Conversation"
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
