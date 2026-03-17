package com.nhuhuy.replee.di

import com.nhuhuy.replee.broadcast.ReplyManager
import com.nhuhuy.replee.broadcast.ReplyManagerImp
import com.nhuhuy.replee.notification.ConversationNotificationFactory
import com.nhuhuy.replee.notification.NotificationFactory
import com.nhuhuy.replee.service.ServiceNotifier
import com.nhuhuy.replee.service.ServiceNotifierImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModuleBinder {
    @Binds
    abstract fun bindReplyHandler(handler: ReplyManagerImp): ReplyManager

    @Binds
    abstract fun bindPushNotificationHandler(handler: ServiceNotifierImp): ServiceNotifier

    @Binds
    abstract fun bindNotificationFactory(factory: ConversationNotificationFactory): NotificationFactory
}

