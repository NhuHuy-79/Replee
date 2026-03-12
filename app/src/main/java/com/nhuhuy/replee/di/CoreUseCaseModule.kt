package com.nhuhuy.replee.di

import com.nhuhuy.core.domain.repository.AccountRepository
import com.nhuhuy.core.domain.usecase.GetAccountByIdUseCase
import com.nhuhuy.core.domain.usecase.GetCurrentAccountUseCase
import com.nhuhuy.core.domain.usecase.SearchAccountByEmailUseCase
import com.nhuhuy.core.domain.usecase.UpdateDeviceTokenUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoreUseCaseModuleProvider {
    @Provides
    @Singleton
    fun provideGetCurrentAccountUseCase(accountRepository: AccountRepository): GetCurrentAccountUseCase {
        return GetCurrentAccountUseCase(accountRepository)
    }

    @Provides
    @Singleton
    fun provideGetAccountByIdUseCase(accountRepository: AccountRepository): GetAccountByIdUseCase {
        return GetAccountByIdUseCase(accountRepository)
    }

    @Provides
    @Singleton
    fun provideSearchAccountsByEmailUseCase(accountRepository: AccountRepository): SearchAccountByEmailUseCase {
        return SearchAccountByEmailUseCase(accountRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateDeviceTokenUseCase(accountRepository: AccountRepository): UpdateDeviceTokenUseCase {
        return UpdateDeviceTokenUseCase(accountRepository)
    }

}