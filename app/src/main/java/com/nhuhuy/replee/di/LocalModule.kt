package com.nhuhuy.replee.di

import android.content.Context
import androidx.room.Room
import com.nhuhuy.replee.core.data.data_store.AppDataStore
import com.nhuhuy.replee.core.data.data_store.AppDataStoreImp
import com.nhuhuy.replee.core.database.CoreDatabase
import com.nhuhuy.replee.core.database.Migration_15_16
import com.nhuhuy.replee.core.database.entity.account.AccountDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModuleProvider {

    @Provides
    @Singleton
    fun provideAppDataStore(@ApplicationContext context: Context): AppDataStore {
        return AppDataStoreImp(context)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CoreDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = CoreDatabase::class.java,
            name = "replee_db"
        ).addMigrations(Migration_15_16)
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideFilePathDao(database: CoreDatabase) = database.provideFilePathDao()


    @Provides
    @Singleton
    fun provideMessageRemoteKeyDao(database: CoreDatabase) = database.provideMessageRemoteKeyDao()

    @Provides
    @Singleton
    fun provideConversationDao(database: CoreDatabase) = database.provideConversationDao()

    @Provides
    @Singleton
    fun provideMessageDao(database: CoreDatabase) = database.provideMessageDao()

    @Provides
    @Singleton
    fun provideAccountDao(database: CoreDatabase): AccountDao {
        return database.provideAccountDao()
    }
}
