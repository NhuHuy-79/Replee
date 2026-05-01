package com.nhuhuy.replee.core.data.di

import com.nhuhuy.replee.core.common.utils.InputValidator
import com.nhuhuy.replee.core.data.utils.ApplicationCoroutineScope
import com.nhuhuy.replee.core.data.utils.IoDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonDataModule {

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
}
