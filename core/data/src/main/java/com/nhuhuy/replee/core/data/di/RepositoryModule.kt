package com.nhuhuy.replee.core.data.di

import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.repository.PresenceRepository
import com.nhuhuy.replee.core.data.repository.AccountRepositoryImp
import com.nhuhuy.replee.core.data.repository.PresenceRepositoryImp
import com.nhuhuy.replee.core.data.repository.SessionManagerImp
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
    abstract fun bindAccountRepository(imp: AccountRepositoryImp): com.nhuhuy.core.domain.repository.AccountRepository

    @Binds
    @Singleton
    abstract fun bindSessionManager(imp: SessionManagerImp): SessionManager

    @Binds
    @Singleton
    abstract fun bindPresenceRepository(imp: PresenceRepositoryImp): PresenceRepository
}
