package com.nhuhuy.replee.di

import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSourceImp
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSourceImp
import com.nhuhuy.replee.core.network.data_source.CloudinaryFileUploader
import com.nhuhuy.replee.core.network.data_source.TokenNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.TokenNetworkDataSourceImp
import com.nhuhuy.replee.core.network.data_source.UploadFileService
import com.nhuhuy.replee.feature_auth.data.data_source.AuthNetworkDataSource
import com.nhuhuy.replee.feature_auth.data.data_source.AuthNetworkDataSourceImp
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSourceImp
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSourceImp
import com.nhuhuy.replee.feature_chat.data.source.message.MessageLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageLocalDataSourceImp
import com.nhuhuy.replee.feature_chat.data.source.message.MessageNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageNetworkDataSourceImp
import com.nhuhuy.replee.feature_chat.data.source.presence.FirebasePresenceDataSource
import com.nhuhuy.replee.feature_chat.data.source.presence.PresenceNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModuleBinder {
    //Chat Module
    @Binds
    abstract fun bindPresenceNetworkDataSource(
        imp: FirebasePresenceDataSource
    ): PresenceNetworkDataSource

    @Binds
    abstract fun bindMessageLocalDataSource(
        imp: MessageLocalDataSourceImp
    ): MessageLocalDataSource

    @Binds
    abstract fun bindMessageNetworkDataSource(
        imp: MessageNetworkDataSourceImp
    ): MessageNetworkDataSource

    @Binds
    abstract fun bindConversationLocalDataSource(
        imp: ConversationLocalDataSourceImp
    ): ConversationLocalDataSource

    @Binds
    abstract fun bindConversationNetworkDataSource(
        imp: ConversationNetworkDataSourceImp
    ): ConversationNetworkDataSource


    //Profile Module
    @Binds
    abstract fun bindCloudinaryFileUploader(impl: CloudinaryFileUploader): UploadFileService

    @Binds
    abstract fun bindAuthNetworkDataSource(imp: AuthNetworkDataSourceImp): AuthNetworkDataSource

    @Binds
    abstract fun bindTokenNetworkDataSource(imp: TokenNetworkDataSourceImp): TokenNetworkDataSource

    @Binds
    abstract fun bindAccountLocalDataSource(imp: AccountLocalDataSourceImp): AccountLocalDataSource

    @Binds
    abstract fun bindAccountNetworkDataSource(imp: AccountNetworkDataSourceImp): AccountNetworkDataSource
}
