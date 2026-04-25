package com.nhuhuy.replee.di


import android.content.Context
import com.nhuhuy.replee.core.common.utils.InputValidator
import com.nhuhuy.replee.core.data.utils.ApplicationCoroutineScope
import com.nhuhuy.replee.core.data.utils.IoDispatcher
import com.nhuhuy.replee.core.network.api.mapper.RequestMapper
import com.nhuhuy.replee.feature_chat.data.worker.WorkerScheduler
import com.nhuhuy.replee.notification.NotificationParser
import com.nhuhuy.replee.worker.WorkerSchedulerImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModuleProvider {
    @Provides
    @Singleton
    fun provideValidator() = InputValidator()

    @Provides
    @Singleton
    @IoDispatcher
    fun provideDispatcherIO() = Dispatchers.IO

    @ApplicationCoroutineScope
    @Singleton
    @Provides
    fun provideApplicationCoroutineScope() = CoroutineScope(
        context = SupervisorJob() + Dispatchers.IO
    )

    @Provides
    @Singleton
    fun provideJson() = Json {
        ignoreUnknownKeys = true
    }
    @Provides
    @Singleton
    fun provideNetworkMapper(json: Json) = RequestMapper(json)

    @Provides
    @Singleton
    fun provideParser() = NotificationParser()

    @Provides
    @Singleton
    fun provideSyncScheduler(@ApplicationContext context: Context): WorkerScheduler =
        WorkerSchedulerImp(context)
}