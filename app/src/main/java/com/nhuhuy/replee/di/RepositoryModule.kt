package com.nhuhuy.replee.di

import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.repository.FileRepository
import com.nhuhuy.core.domain.repository.PresenceRepository
import com.nhuhuy.replee.core.data.repository.AccountRepositoryImp
import com.nhuhuy.replee.core.data.repository.PresenceRepositoryImp
import com.nhuhuy.replee.core.data.repository.SessionManagerImp
import com.nhuhuy.replee.feature_auth.data.repository.AuthRepositoryImp
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.data.SyncManagerImp
import com.nhuhuy.replee.feature_chat.data.repository.ActionRepositoryImp
import com.nhuhuy.replee.feature_chat.data.repository.PushNotificationRepositoryImp
import com.nhuhuy.replee.feature_chat.data.repository.conversation.ConversationRepositoryImp
import com.nhuhuy.replee.feature_chat.data.repository.conversation.OptionRepositoryImp
import com.nhuhuy.replee.feature_chat.data.repository.message.FileRepositoryImp
import com.nhuhuy.replee.feature_chat.data.repository.message.MessageRepositoryImp
import com.nhuhuy.replee.feature_chat.data.repository.metadata.MetaDataRepositoryImp
import com.nhuhuy.replee.feature_chat.data.worker.MessageWorkerScheduler
import com.nhuhuy.replee.feature_chat.data.worker.MessageWorkerSchedulerImp
import com.nhuhuy.replee.feature_chat.domain.repository.ActionRepository
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MetaDataRepository
import com.nhuhuy.replee.feature_chat.domain.repository.OptionRepository
import com.nhuhuy.replee.feature_chat.domain.repository.PushNotificationRepository
import com.nhuhuy.replee.feature_profile.data.repository.ProfileRepositoryImp
import com.nhuhuy.replee.feature_profile.data.worker.ProfileScheduler
import com.nhuhuy.replee.feature_profile.data.worker.ProfileSchedulerImp
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

    @Binds
    abstract fun bindFileRepository(imp: FileRepositoryImp): FileRepository

    //Core
    @Binds
    abstract fun bindPushRepository(impl: PushNotificationRepositoryImp): PushNotificationRepository

    @Binds
    abstract fun bindAccountRepository(imp: AccountRepositoryImp): com.nhuhuy.core.domain.repository.AccountRepository

    @Binds
    abstract fun bindSessionManager(imp: SessionManagerImp): SessionManager
    //Chat

    @Binds
    abstract fun bindMetaDataRepository(imp: MetaDataRepositoryImp): MetaDataRepository

    @Binds
    abstract fun bindMessageScheduler(imp: MessageWorkerSchedulerImp): MessageWorkerScheduler

    @Binds
    abstract fun bindActionRepository(imp: ActionRepositoryImp): ActionRepository

    @Binds
    abstract fun bindPresenceRepository(presenceRepositoryImp: PresenceRepositoryImp): PresenceRepository

    @Binds
    abstract fun bindConversationSettingRepository(
        imp: OptionRepositoryImp
    ): OptionRepository

    @Binds
    abstract fun bindMessageRepository(messageRepositoryImp: MessageRepositoryImp): MessageRepository

    @Binds
    abstract fun bindConversationRepository(conversationRepositoryImp: ConversationRepositoryImp): ConversationRepository

    @Binds
    abstract fun bindSyncManager(syncManagerImp: SyncManagerImp): SyncManager

    //Profile
    @Binds
    abstract fun bindProfileRepository(profileRepositoryImp: ProfileRepositoryImp): ProfileRepository

    @Binds
    abstract fun bindProfileSchedular(imp: ProfileSchedulerImp): ProfileScheduler

}
