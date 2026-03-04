package com.nhuhuy.replee.feature_chat.di

import com.nhuhuy.replee.feature_chat.data.NotifyService
import com.nhuhuy.replee.feature_chat.data.NotifyServiceImp
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.data.SyncManagerImp
import com.nhuhuy.replee.feature_chat.data.repository.ConversationRepositoryImp
import com.nhuhuy.replee.feature_chat.data.repository.ConversationSettingRepositoryImp
import com.nhuhuy.replee.feature_chat.data.repository.MessageRepositoryImp
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationSettingRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatModuleBinder {
    @Binds
    abstract fun bindNotifyService(notifyServiceImp: NotifyServiceImp): NotifyService

    @Binds
    abstract fun bindConversationSettingRepository(
        imp: ConversationSettingRepositoryImp
    ): ConversationSettingRepository

    @Binds
    abstract fun bindMessageRepository(messageRepositoryImp: MessageRepositoryImp): MessageRepository

    @Binds
    abstract fun bindConversationRepository(conversationRepositoryImp: ConversationRepositoryImp): ConversationRepository

    @Binds
    abstract fun bindSyncManager(syncManagerImp: SyncManagerImp): SyncManager
}