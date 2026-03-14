package com.nhuhuy.replee.di


import android.content.Context
import com.nhuhuy.replee.core.common.utils.InputValidator
import com.nhuhuy.replee.core.network.api.mapper.RequestMapper
import com.nhuhuy.replee.notification.NotificationParser
import com.nhuhuy.replee.worker.ListenConversationsManager
import com.nhuhuy.replee.worker.ListenConversationsManagerImp
import com.nhuhuy.replee.worker.WorkerScheduler
import com.nhuhuy.replee.worker.WorkerSchedulerImp
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModuleBinder {
    @Binds
    abstract fun bindListenConversationsManager(imp: ListenConversationsManagerImp): ListenConversationsManager

}

@Module
@InstallIn(SingletonComponent::class)
object AppModuleProvider {
    @Provides
    @Singleton
    fun provideValidator() = InputValidator()

    @Provides
    @Singleton
    fun provideDispatcherIO() = Dispatchers.IO



    @Provides
    @Singleton
    fun provideJson() = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }
    @Provides
    @Singleton
    fun provideNetworkMapper(json: Json) = RequestMapper(json)

    @Provides
    @Singleton
    fun provideParser(mapper: RequestMapper) = NotificationParser(mapper)

    @Provides
    @Singleton
    fun provideSyncScheduler(@ApplicationContext context: Context): WorkerScheduler =
        WorkerSchedulerImp(context)
}