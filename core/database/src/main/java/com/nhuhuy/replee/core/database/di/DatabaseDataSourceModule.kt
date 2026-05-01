package com.nhuhuy.replee.core.database.di

import com.nhuhuy.replee.core.database.LocalTransactionRunner
import com.nhuhuy.replee.core.database.LocalTransactionRunnerImp
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSourceImp
import com.nhuhuy.replee.core.database.data_source.ChatActionDataSource
import com.nhuhuy.replee.core.database.data_source.ChatActionDataSourceImp
import com.nhuhuy.replee.core.database.data_source.ConversationLocalDataSource
import com.nhuhuy.replee.core.database.data_source.ConversationLocalDataSourceImp
import com.nhuhuy.replee.core.database.data_source.MessageLocalDataSource
import com.nhuhuy.replee.core.database.data_source.MessageLocalDataSourceImp
import com.nhuhuy.replee.core.database.data_source.ProfileLocalDataSource
import com.nhuhuy.replee.core.database.data_source.ProfileLocalDataSourceImp
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

    @Binds
    @Singleton
    abstract fun bindChatActionDataSource(imp: ChatActionDataSourceImp): ChatActionDataSource

    @Binds
    @Singleton
    abstract fun bindMessageLocalDataSource(imp: MessageLocalDataSourceImp): MessageLocalDataSource

    @Binds
    @Singleton
    abstract fun bindConversationLocalDataSource(imp: ConversationLocalDataSourceImp): ConversationLocalDataSource

    @Binds
    @Singleton
    abstract fun bindProfileLocalDataSource(imp: ProfileLocalDataSourceImp): ProfileLocalDataSource
}
