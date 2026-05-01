package com.nhuhuy.replee.feature_chat.di

import com.nhuhuy.core.domain.repository.FileRepository
import com.nhuhuy.replee.feature_chat.data.repository.ChatActionRepositoryImp
import com.nhuhuy.replee.feature_chat.data.repository.PushNotificationRepositoryImp
import com.nhuhuy.replee.feature_chat.data.repository.conversation.ConversationRepositoryImp
import com.nhuhuy.replee.feature_chat.data.repository.conversation.OptionRepositoryImp
import com.nhuhuy.replee.feature_chat.data.repository.message.FileRepositoryImp
import com.nhuhuy.replee.feature_chat.data.repository.message.MessageRepositoryImp
import com.nhuhuy.replee.feature_chat.data.repository.metadata.MetaDataRepositoryImp
import com.nhuhuy.replee.feature_chat.domain.repository.ChatActionRepository
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MetaDataRepository
import com.nhuhuy.replee.feature_chat.domain.repository.OptionRepository
import com.nhuhuy.replee.feature_chat.domain.repository.PushNotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatModule {

    @Binds
    @Singleton
    abstract fun bindFileRepository(imp: FileRepositoryImp): FileRepository

    @Binds
    @Singleton
    abstract fun bindMetaDataRepository(imp: MetaDataRepositoryImp): MetaDataRepository

    @Binds
    @Singleton
    abstract fun bindActionRepository(imp: ChatActionRepositoryImp): ChatActionRepository

    @Binds
    @Singleton
    abstract fun bindConversationSettingRepository(imp: OptionRepositoryImp): OptionRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(messageRepositoryImp: MessageRepositoryImp): MessageRepository

    @Binds
    @Singleton
    abstract fun bindConversationRepository(conversationRepositoryImp: ConversationRepositoryImp): ConversationRepository

    @Binds
    @Singleton
    abstract fun bindPushRepository(impl: PushNotificationRepositoryImp): PushNotificationRepository
}
