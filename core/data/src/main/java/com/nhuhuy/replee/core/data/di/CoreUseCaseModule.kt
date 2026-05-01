package com.nhuhuy.replee.core.data.di

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
object CoreUseCaseModule {

    @Provides
    @Singleton
    fun provideGetCurrentAccountUseCase(accountRepository: AccountRepository) =
        GetCurrentAccountUseCase(accountRepository)

    @Provides
    @Singleton
    fun provideGetAccountByIdUseCase(accountRepository: AccountRepository) =
        GetAccountByIdUseCase(accountRepository)

    @Provides
    @Singleton
    fun provideSearchAccountsByEmailUseCase(accountRepository: AccountRepository) =
        SearchAccountByEmailUseCase(accountRepository)

    @Provides
    @Singleton
    fun provideUpdateDeviceTokenUseCase(accountRepository: AccountRepository) =
        UpdateDeviceTokenUseCase(accountRepository)
}
