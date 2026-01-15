package com.nhuhuy.replee.di

import com.nhuhuy.replee.core.firebase.network.mapper.NetworkMapper
import com.nhuhuy.replee.notification.ConversationNotificationFactory
import com.nhuhuy.replee.notification.NotificationFactory
import com.nhuhuy.replee.notification.NotificationParser
import com.nhuhuy.replee.service.PushNotificationHandler
import com.nhuhuy.replee.service.PushNotificationHandlerImp
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {
    @Binds
    abstract fun bindPushNotificationHandler(handler: PushNotificationHandlerImp): PushNotificationHandler

    @Binds
    abstract fun bindNotificationFactory(factory: ConversationNotificationFactory): NotificationFactory
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
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
}
