package com.nhuhuy.replee.feature_chat.data.worker

import android.content.Context
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject

interface MessageWorkerScheduler {
    suspend fun scheduleMessageSyncWorker(messageId: String, conversationId: String)
}


class MessageWorkerSchedulerImp @Inject constructor(
    @ApplicationContext private val context: Context,
) : MessageWorkerScheduler {
    private val workManager by lazy {
        WorkManager.getInstance(context)
    }

    override suspend fun scheduleMessageSyncWorker(messageId: String, conversationId: String) {

    }

}