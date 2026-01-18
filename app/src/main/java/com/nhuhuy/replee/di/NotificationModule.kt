package com.nhuhuy.replee.di

import com.nhuhuy.replee.notification.ConversationNotificationFactory
import com.nhuhuy.replee.notification.NotificationFactory
import com.nhuhuy.replee.receiver.ReceiverHandler
import com.nhuhuy.replee.receiver.ReceiverHandlerImp
import com.nhuhuy.replee.service.PushNotificationHandler
import com.nhuhuy.replee.service.PushNotificationHandlerImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {
    @Binds
    abstract fun bindReplyHandler(handler: ReceiverHandlerImp): ReceiverHandler

    @Binds
    abstract fun bindPushNotificationHandler(handler: PushNotificationHandlerImp): PushNotificationHandler

    @Binds
    abstract fun bindNotificationFactory(factory: ConversationNotificationFactory): NotificationFactory
}

