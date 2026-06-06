package com.nhuhuy.replee.core.network.di

import com.nhuhuy.replee.core.network.data_source.account.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.account.AccountNetworkDataSourceImp
import com.nhuhuy.replee.core.network.data_source.account.FirebasePresenceDataSource
import com.nhuhuy.replee.core.network.data_source.account.PresenceNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.account.ProfileNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.account.ProfileNetworkDataSourceImp
import com.nhuhuy.replee.core.network.data_source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.conversation.ConversationNetworkDataSourceImp
import com.nhuhuy.replee.core.network.data_source.conversation.MetaDataNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.conversation.MetaDataNetworkDataSourceImpl
import com.nhuhuy.replee.core.network.data_source.file.UploadFileService
import com.nhuhuy.replee.core.network.data_source.message.MessageNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.message.MessageNetworkDataSourceImp
import com.nhuhuy.replee.core.network.data_source.message.PagingMessageNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.message.PagingMessageNetworkDataSourceImp
import com.nhuhuy.replee.core.network.data_source.message.PushNotificationNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.message.PushNotificationNetworkDataSourceImp
import com.nhuhuy.replee.core.network.data_source.transaction.NetworkMultipleWriteRunner
import com.nhuhuy.replee.core.network.data_source.transaction.NetworkMultipleWriteRunnerImpl
import com.nhuhuy.replee.core.network.data_source.transaction.NetworkTransactionRunner
import com.nhuhuy.replee.core.network.data_source.transaction.NetworkTransactionRunnerImpl
import com.nhuhuy.replee.core.network.imp.RetrofitUploader
import com.nhuhuy.replee.core.network.quailify.Retrofit
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindAccountNetworkDataSource(imp: AccountNetworkDataSourceImp): AccountNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindPresenceNetworkDataSource(imp: FirebasePresenceDataSource): PresenceNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindTokenNetworkDataSource(imp: PushNotificationNetworkDataSourceImp): PushNotificationNetworkDataSource

    @Binds
    @Singleton
    @Retrofit
    abstract fun bindRetrofitFileUploader(imp: RetrofitUploader): UploadFileService

    @Binds
    @Singleton
    abstract fun bindMetaDataNetworkDataSource(imp: MetaDataNetworkDataSourceImpl): MetaDataNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindMessageNetworkDataSource(imp: MessageNetworkDataSourceImp): MessageNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindConversationNetworkDataSource(imp: ConversationNetworkDataSourceImp): ConversationNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindPageMessageNetworkDataSource(imp: PagingMessageNetworkDataSourceImp): PagingMessageNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindProfileNetworkDataSource(imp: ProfileNetworkDataSourceImp): ProfileNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindNetworkTransactionRunner(impl: NetworkTransactionRunnerImpl): NetworkTransactionRunner

    @Binds
    @Singleton
    abstract fun bindNetworkMultipleWriteRunner(impl: NetworkMultipleWriteRunnerImpl): NetworkMultipleWriteRunner
}
