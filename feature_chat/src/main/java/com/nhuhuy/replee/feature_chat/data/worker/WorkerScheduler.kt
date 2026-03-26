package com.nhuhuy.replee.feature_chat.data.worker

interface WorkerScheduler {
    fun scheduleFileSyncWorker()
    fun scheduleMessageSyncWorker()
    fun scheduleConversationSyncWorker()
}
