package com.nhuhuy.replee.core.sync.di

import com.nhuhuy.replee.core.domain.worker.WorkerScheduler
import com.nhuhuy.replee.core.sync.SyncManager
import com.nhuhuy.replee.core.sync.SyncManagerImp
import com.nhuhuy.replee.core.sync.WorkerSchedulerImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {

    @Binds
    @Singleton
    abstract fun bindSyncManager(syncManagerImp: SyncManagerImp): SyncManager

    @Binds
    @Singleton
    abstract fun bindWorkerScheduler(workerSchedulerImp: WorkerSchedulerImp): WorkerScheduler
}
