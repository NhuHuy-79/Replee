package com.nhuhuy.replee.di

import com.nhuhuy.replee.core.common.data.repository.AccountRepositoryImp
import com.nhuhuy.replee.core.common.data.repository.PushNotificationRepository
import com.nhuhuy.replee.core.common.data.repository.PushNotificationRepositoryImp
import com.nhuhuy.replee.feature_auth.data.repository.AuthRepositoryImp
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import com.nhuhuy.replee.feature_chat.data.NotifyService
import com.nhuhuy.replee.feature_chat.data.NotifyServiceImp
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.data.SyncManagerImp
import com.nhuhuy.replee.feature_chat.data.repository.ConversationRepositoryImp
import com.nhuhuy.replee.feature_chat.data.repository.ConversationSettingRepositoryImp
import com.nhuhuy.replee.feature_chat.data.repository.MessageRepositoryImp
import com.nhuhuy.replee.feature_chat.data.repository.PresenceRepositoryImp
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationSettingRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import com.nhuhuy.replee.feature_chat.domain.repository.PresenceRepository
import com.nhuhuy.replee.feature_profile.data.repository.ProfileRepositoryImp
import com.nhuhuy.replee.feature_profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModuleBinder {
    //Auth
    @Binds
    abstract fun bindAuthRepository(imp: AuthRepositoryImp): AuthRepository


    //Core
    @Binds
    abstract fun bindPushRepository(impl: PushNotificationRepositoryImp): PushNotificationRepository

    @Binds
    abstract fun bindAccountRepository(imp: AccountRepositoryImp): com.nhuhuy.core.domain.repository.AccountRepository

    //Chat
    @Binds
    abstract fun bindNotifyService(notifyServiceImp: NotifyServiceImp): NotifyService

    @Binds
    abstract fun bindPresenceRepository(presenceRepositoryImp: PresenceRepositoryImp): PresenceRepository

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

    //Profile
    @Binds
    abstract fun bindProfileRepository(profileRepositoryImp: ProfileRepositoryImp): ProfileRepository

}
