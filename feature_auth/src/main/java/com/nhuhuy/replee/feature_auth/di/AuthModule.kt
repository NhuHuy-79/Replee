package com.nhuhuy.replee.feature_auth.di

import com.nhuhuy.replee.feature_auth.data.data_source.AuthNetworkDataSource
import com.nhuhuy.replee.feature_auth.data.data_source.AuthNetworkDataSourceImp
import com.nhuhuy.replee.feature_auth.data.repository.AuthRepositoryImp
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(imp: AuthRepositoryImp): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAuthNetworkDataSource(imp: AuthNetworkDataSourceImp): AuthNetworkDataSource
}
