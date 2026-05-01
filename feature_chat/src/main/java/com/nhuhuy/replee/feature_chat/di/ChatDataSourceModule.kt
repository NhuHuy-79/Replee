package com.nhuhuy.replee.feature_chat.di

import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSourceImp
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSourceImp
import com.nhuhuy.replee.feature_chat.data.source.message.ChatActionDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.ChatActionDataSourceImp
import com.nhuhuy.replee.feature_chat.data.source.message.MessageLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageLocalDataSourceImp
import com.nhuhuy.replee.feature_chat.data.source.message.MessageNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageNetworkDataSourceImp
import com.nhuhuy.replee.feature_chat.data.source.metadata.MetaDataNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.metadata.MetaDataNetworkDataSourceImpl
import com.nhuhuy.replee.feature_chat.data.source.paging.PagingMessageNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.paging.PagingMessageNetworkDataSourceImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindChatActionDataSource(imp: ChatActionDataSourceImp): ChatActionDataSource

    @Binds
    @Singleton
    abstract fun bindMetaDataNetworkDataSource(imp: MetaDataNetworkDataSourceImpl): MetaDataNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindMessageLocalDataSource(imp: MessageLocalDataSourceImp): MessageLocalDataSource

    @Binds
    @Singleton
    abstract fun bindMessageNetworkDataSource(imp: MessageNetworkDataSourceImp): MessageNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindConversationLocalDataSource(imp: ConversationLocalDataSourceImp): ConversationLocalDataSource

    @Binds
    @Singleton
    abstract fun bindConversationNetworkDataSource(imp: ConversationNetworkDataSourceImp): ConversationNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindPageMessageNetworkDataSource(imp: PagingMessageNetworkDataSourceImp): PagingMessageNetworkDataSource
}
