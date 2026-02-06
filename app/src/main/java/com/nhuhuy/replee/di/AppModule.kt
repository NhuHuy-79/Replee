package com.nhuhuy.replee.di

import android.content.Context
import com.nhuhuy.core.domain.utils.Logger
import com.nhuhuy.replee.GoogleIdTokenProviderImp
import com.nhuhuy.replee.core.common.data.preferences.AppPreferences
import com.nhuhuy.replee.core.common.utils.LoggerImp
import com.nhuhuy.replee.core.common.utils.Validator
import com.nhuhuy.replee.core.firebase.network.mapper.NetworkMapper
import com.nhuhuy.replee.feature_auth.data.GoogleIdTokenProvider
import com.nhuhuy.replee.notification.NotificationParser
import com.nhuhuy.replee.worker.WorkerScheduler
import com.nhuhuy.replee.worker.WorkerSchedulerImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideValidator() = Validator()

    @Provides
    @Singleton
    fun provideDispatcherIO() = Dispatchers.IO

    @Provides
    @Singleton
    fun provideGoogleIdTokenProvider(
        logger: Logger,
        ioDispatcher: CoroutineDispatcher,
        @ApplicationContext context: Context
    ): GoogleIdTokenProvider = GoogleIdTokenProviderImp(
        context = context,
        logger = logger,
        ioDispatcher = ioDispatcher
    )

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context) = AppPreferences(context)

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