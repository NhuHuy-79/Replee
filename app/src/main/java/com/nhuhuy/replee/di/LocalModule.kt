package com.nhuhuy.replee.di

import android.content.Context
import androidx.room.Room
import com.nhuhuy.replee.core.firebase.Constant
import com.nhuhuy.replee.core.database.entity.account.AccountDao
import com.nhuhuy.replee.core.database.entity.account.AccountDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) : AccountDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = AccountDatabase::class.java,
            name = "account_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAccountDao(database: AccountDatabase) : AccountDao {
        return database.provideDao()
    }
}