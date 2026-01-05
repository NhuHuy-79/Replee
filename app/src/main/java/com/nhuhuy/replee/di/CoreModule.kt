package com.nhuhuy.replee.di

import com.nhuhuy.replee.core.common.Validator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoreModule {
    @Provides
    @Singleton
    fun provideValidator() = Validator()

    @Provides
    @Singleton
    fun provideDispatcherIO() = Dispatchers.IO
}