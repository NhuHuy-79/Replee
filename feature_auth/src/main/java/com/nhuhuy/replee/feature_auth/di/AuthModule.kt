package com.nhuhuy.replee.feature_auth.di

import com.nhuhuy.replee.feature_auth.data.repository.AuthRepositoryImp
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModuleBinder {
    @Binds
    abstract fun bindAuthRepository(imp: AuthRepositoryImp): AuthRepository
}

