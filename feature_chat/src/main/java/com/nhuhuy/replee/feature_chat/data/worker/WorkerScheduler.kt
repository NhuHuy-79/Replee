package com.nhuhuy.replee.feature_chat.data.worker

interface WorkerScheduler {
    fun scheduleConversationSyncWorker()
    fun scheduleFileSyncWorker()
    fun scheduleMessageSyncWorker()
    fun scheduleMessageActionWorker()
    fun scheduleSaveMessageWorker(conversationId: String)
}
