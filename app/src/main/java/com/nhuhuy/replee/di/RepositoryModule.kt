package com.nhuhuy.replee.di

import com.nhuhuy.replee.core.common.data.repository.AccountRepository
import com.nhuhuy.replee.core.common.data.repository.AccountRepositoryImp
import com.nhuhuy.replee.feature_auth.data.repository.AuthRepositoryImp
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.data.SyncManagerImp
import com.nhuhuy.replee.feature_chat.data.repository.ConversationRepositoryImp
import com.nhuhuy.replee.feature_chat.data.repository.MessageRepositoryImp
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import com.nhuhuy.replee.feature_profile.data.repository.ProfileRepositoryImp
import com.nhuhuy.replee.feature_profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindSyncManager(syncManagerImp: SyncManagerImp): SyncManager

    @Binds
    abstract fun bindAuthRepository(authRepositoryImp: AuthRepositoryImp): AuthRepository

    @Binds
    abstract fun bindConversationRepository(conversationRepositoryImp: ConversationRepositoryImp): ConversationRepository

    @Binds
    abstract fun bindProfileRepository(profileRepositoryImp: ProfileRepositoryImp): ProfileRepository

    @Binds
    abstract fun bindAccountRepository(accountRepository: AccountRepositoryImp) : AccountRepository

    @Binds
    abstract fun bindMessageRepository(messageRepositoryImp: MessageRepositoryImp): MessageRepository


}