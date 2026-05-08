package com.nhuhuy.replee.feature_chat

import com.nhuhuy.replee.feature_chat.data.PaginatorRepositoryImpl
import com.nhuhuy.replee.feature_chat.domain.repository.PaginatorRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureChatModule {
    @Binds
    @Singleton
    abstract fun bindPaginatorRepository(imp: PaginatorRepositoryImpl): PaginatorRepository
}