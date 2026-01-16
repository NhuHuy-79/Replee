package com.nhuhuy.replee.di

import android.content.Context
import com.nhuhuy.replee.core.common.data.preferences.AppPreferences
import com.nhuhuy.replee.core.common.utils.Validator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context) = AppPreferences(context)
}


