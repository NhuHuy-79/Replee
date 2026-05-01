package com.nhuhuy.replee.core.database.di

import com.nhuhuy.replee.core.database.LocalTransactionRunner
import com.nhuhuy.replee.core.database.LocalTransactionRunnerImp
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSourceImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindAccountLocalDataSource(imp: AccountLocalDataSourceImp): AccountLocalDataSource

    @Binds
    @Singleton
    abstract fun bindLocalTransactionRunner(imp: LocalTransactionRunnerImp): LocalTransactionRunner
}
