package com.nhuhuy.replee.core.data.di

import com.nhuhuy.replee.core.data.repository.AccountRepositoryImp
import com.nhuhuy.replee.core.data.repository.PresenceRepositoryImp
import com.nhuhuy.replee.core.data.repository.ProfileRepositoryImp
import com.nhuhuy.replee.core.data.repository.SessionManagerImp
import com.nhuhuy.replee.core.data.repository.chat.ChatActionRepositoryImp
import com.nhuhuy.replee.core.data.repository.chat.ConversationRepositoryImp
import com.nhuhuy.replee.core.data.repository.chat.FileRepositoryImp
import com.nhuhuy.replee.core.data.repository.chat.MessageRepositoryImp
import com.nhuhuy.replee.core.data.repository.chat.MetaDataRepositoryImp
import com.nhuhuy.replee.core.data.repository.chat.OptionRepositoryImp
import com.nhuhuy.replee.core.data.repository.chat.PushNotificationRepositoryImp
import com.nhuhuy.replee.core.domain.SessionManager
import com.nhuhuy.replee.core.domain.repository.AccountActionRepository
import com.nhuhuy.replee.core.domain.repository.AccountQueryRepository
import com.nhuhuy.replee.core.domain.repository.AccountRepository
import com.nhuhuy.replee.core.domain.repository.ChatActionRepository
import com.nhuhuy.replee.core.domain.repository.ConversationActionRepository
import com.nhuhuy.replee.core.domain.repository.ConversationQueryRepository
import com.nhuhuy.replee.core.domain.repository.ConversationRepository
import com.nhuhuy.replee.core.domain.repository.FileRepository
import com.nhuhuy.replee.core.domain.repository.MessageActionRepository
import com.nhuhuy.replee.core.domain.repository.MessageQueryRepository
import com.nhuhuy.replee.core.domain.repository.MessageRepository
import com.nhuhuy.replee.core.domain.repository.MetaDataRepository
import com.nhuhuy.replee.core.domain.repository.OptionRepository
import com.nhuhuy.replee.core.domain.repository.PresenceRepository
import com.nhuhuy.replee.core.domain.repository.ProfileActionRepository
import com.nhuhuy.replee.core.domain.repository.ProfileQueryRepository
import com.nhuhuy.replee.core.domain.repository.ProfileRepository
import com.nhuhuy.replee.core.domain.repository.PushNotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAccountRepository(imp: AccountRepositoryImp): AccountRepository

    @Binds
    @Singleton
    abstract fun bindAccountQueryRepository(imp: AccountRepositoryImp): AccountQueryRepository

    @Binds
    @Singleton
    abstract fun bindAccountActionRepository(imp: AccountRepositoryImp): AccountActionRepository


    @Binds
    @Singleton
    abstract fun bindSessionManager(imp: SessionManagerImp): SessionManager

    @Binds
    @Singleton
    abstract fun bindPresenceRepository(imp: PresenceRepositoryImp): PresenceRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(imp: ProfileRepositoryImp): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindProfileQueryRepository(imp: ProfileRepositoryImp): ProfileQueryRepository

    @Binds
    @Singleton
    abstract fun bindProfileActionRepository(imp: ProfileRepositoryImp): ProfileActionRepository

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
    abstract fun bindMessageRepository(imp: MessageRepositoryImp): MessageRepository

    @Binds
    @Singleton
    abstract fun bindMessageQueryRepository(imp: MessageRepositoryImp): MessageQueryRepository

    @Binds
    @Singleton
    abstract fun bindMessageActionRepository(imp: MessageRepositoryImp): MessageActionRepository

    @Binds
    @Singleton
    abstract fun bindConversationRepository(imp: ConversationRepositoryImp): ConversationRepository

    @Binds
    @Singleton
    abstract fun bindConversationQueryRepository(imp: ConversationRepositoryImp): ConversationQueryRepository

    @Binds
    @Singleton
    abstract fun bindConversationActionRepository(imp: ConversationRepositoryImp): ConversationActionRepository

    @Binds
    @Singleton
    abstract fun bindPushRepository(impl: PushNotificationRepositoryImp): PushNotificationRepository
}
