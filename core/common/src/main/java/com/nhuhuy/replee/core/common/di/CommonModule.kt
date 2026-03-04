package com.nhuhuy.replee.core.common.di

import com.nhuhuy.replee.core.common.data.repository.AccountRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonModuleBinder {
    @Binds
    abstract fun bindAccountRepository(imp: AccountRepositoryImp): com.nhuhuy.core.domain.repository.AccountRepository

}