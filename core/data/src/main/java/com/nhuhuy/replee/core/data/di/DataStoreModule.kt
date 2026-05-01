package com.nhuhuy.replee.core.data.di

import android.content.Context
import com.nhuhuy.replee.core.data.data_store.AppDataStore
import com.nhuhuy.replee.core.data.data_store.AppDataStoreImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideAppDataStore(@ApplicationContext context: Context): AppDataStore {
        return AppDataStoreImp(context)
    }
}
