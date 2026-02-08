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
import coil3.util.DebugLogger
import com.nhuhuy.replee.worker.WorkerScheduler
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
        createNotificationChannel(this)
        Timber.plant(Timber.DebugTree())
        workerScheduler.scheduleMessageSyncWorker()
        workerScheduler.scheduleConversationSyncWorker()

    }

    /* private fun disableFirestoreCacheSetting() {
         val settings = FirebaseFirestoreSettings.Builder()
             .setPersistenceEnabled(false)
             .build()

         FirebaseFirestore.getInstance().firestoreSettings = settings
     }*/

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
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context = context, percent = 0.1)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .maxSizePercent(0.1)
                    .directory(fileDirectory)
                    .build()
            }
            .logger(DebugLogger())
            .build()
    }
}
