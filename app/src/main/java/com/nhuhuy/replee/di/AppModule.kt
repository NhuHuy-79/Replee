package com.nhuhuy.replee.di

import android.content.Context
import com.nhuhuy.core.domain.utils.Logger
import com.nhuhuy.replee.core.common.data.UriConverter
import com.nhuhuy.replee.core.common.data.preferences.AppPreferences
import com.nhuhuy.replee.core.common.qualifier.AppCoroutineScope
import com.nhuhuy.replee.core.common.utils.InputValidator
import com.nhuhuy.replee.core.common.utils.LoggerImp
import com.nhuhuy.replee.core.firebase.data_source.CloudifyFileUploadService
import com.nhuhuy.replee.core.firebase.network.mapper.NetworkMapper
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AbstractAppModule {
    @Binds
    abstract fun bindListenConversationsManager(imp: ListenConversationsManagerImp): ListenConversationsManager

}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @AppCoroutineScope
    @Provides
    fun provideAppCoroutineScope(): CoroutineScope = CoroutineScope(
        Dispatchers.IO + SupervisorJob()
    )
    @Provides
    @Singleton
    fun provideValidator() = InputValidator()

    @Provides
    @Singleton
    fun provideDispatcherIO() = Dispatchers.IO


    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context) = AppPreferences(context)

    @Provides
    @Singleton
    fun provideCloudinaryUploadService() = CloudifyFileUploadService()

    @Provides
    @Singleton
    fun provideUriToFileConverter(
        @ApplicationContext context: Context,
        ioDispatcher: CoroutineDispatcher
    ) =
        UriConverter(context, ioDispatcher)

    @Provides
    @Singleton
    fun provideJson() = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }
    @Provides
    @Singleton
    fun provideNetworkMapper(json: Json) = NetworkMapper(json)

    @Provides
    @Singleton
    fun provideParser(mapper: NetworkMapper) = NotificationParser(mapper)

    @Provides
    @Singleton
    fun provideSyncScheduler(@ApplicationContext context: Context): WorkerScheduler =
        WorkerSchedulerImp(context)

    @Provides
    @Singleton
    fun provideLogger(): Logger = LoggerImp()
}