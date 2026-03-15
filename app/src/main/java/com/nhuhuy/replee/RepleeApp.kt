package com.nhuhuy.replee

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.cloudinary.android.MediaManager
import com.nhuhuy.replee.feature_chat.data.worker.WorkerScheduler
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltAndroidApp
class RepleeApp() : Application(), Configuration.Provider, SingletonImageLoader.Factory {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()

    @Inject lateinit var workerScheduler: WorkerScheduler

    override fun onCreate() {
        super.onCreate()
        //Create notification channel
        createNotificationChannel(this)

        //Initialize cloudinary for file uploading
        initializeCloudinary()

        //Timber for debug logging
        Timber.plant(Timber.DebugTree())

        //Work manager Scheduling
        workerScheduler.scheduleMessageSyncWorker()
        workerScheduler.scheduleConversationSyncWorker()
    }

    /* private fun disableFirestoreCacheSetting() {
         val settings = FirebaseFirestoreSettings.Builder()
             .setPersistenceEnabled(false)
             .build()

         FirebaseFirestore.getInstance().firestoreSettings = settings
     }*/

    private fun initializeCloudinary() {
        val config = mapOf(
            "cloud_name" to "dgq6g8u5h"
        )

        MediaManager.init(this, config)
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            context.getString(R.string.notification_channel),
            "Chat Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Conversation"
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        val fileDirectory = File(
            context.cacheDir,
            "replee_image"
        )
        return ImageLoader.Builder(context)
            .crossfade(false)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context = context, percent = 0.25)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .maxSizePercent(0.25)
                    .directory(fileDirectory)
                    .build()
            }
            .apply {
                if (BuildConfig.DEBUG) {
                    logger(DebugLogger())
                }
            }
            .build()
    }
}
