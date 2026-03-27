package com.nhuhuy.replee.feature_chat.data.worker

import com.nhuhuy.replee.feature_chat.domain.model.message.Message

interface WorkerScheduler {
    fun scheduleFileSyncWorker()
    fun scheduleMessageSyncWorker()
    fun scheduleConversationSyncWorker()
    fun scheduleDeleteMessage(message: Message)

}
