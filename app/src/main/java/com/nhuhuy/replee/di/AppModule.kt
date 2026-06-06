package com.nhuhuy.replee.di

import com.nhuhuy.replee.feature_chat.data.NotificationManager
import com.nhuhuy.replee.helper.NotificationManagerImp
import com.nhuhuy.replee.notification.NotificationParser
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindNotificationHelper(imp: NotificationManagerImp): NotificationManager
}

@Module
@InstallIn(SingletonComponent::class)
object AppModuleProvider {

    @Provides
    @Singleton
    fun provideParser() = NotificationParser()
}
