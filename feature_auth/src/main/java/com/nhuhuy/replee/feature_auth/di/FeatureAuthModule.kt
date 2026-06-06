package com.nhuhuy.replee.feature_auth.di

import com.nhuhuy.replee.feature_auth.data.data_source.AuthNetworkDataSource
import com.nhuhuy.replee.feature_auth.data.data_source.AuthNetworkDataSourceImp
import com.nhuhuy.replee.feature_auth.data.repository.AuthRepositoryImp
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureAuthModule {
    @dagger.Binds
    abstract fun bindAuthNetworkDataSource(impl: AuthNetworkDataSourceImp): AuthNetworkDataSource

    @dagger.Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImp): AuthRepository
}
