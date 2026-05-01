package com.nhuhuy.replee.feature_chat.di

import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.data.SyncManagerImp
import com.nhuhuy.replee.feature_chat.data.worker.MessageWorkerScheduler
import com.nhuhuy.replee.feature_chat.data.worker.MessageWorkerSchedulerImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatSyncModule {

    @Binds
    @Singleton
    abstract fun bindSyncManager(syncManagerImp: SyncManagerImp): SyncManager

    @Binds
    @Singleton
    abstract fun bindMessageScheduler(imp: MessageWorkerSchedulerImp): MessageWorkerScheduler
}
