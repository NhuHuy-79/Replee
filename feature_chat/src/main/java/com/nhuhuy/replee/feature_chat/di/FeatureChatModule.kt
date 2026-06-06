package com.nhuhuy.replee.feature_chat.di

import com.nhuhuy.replee.core.common.di.ScopeId
import com.nhuhuy.replee.feature_chat.data.PaginatorRepositoryImpl
import com.nhuhuy.replee.feature_chat.domain.repository.PaginatorRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureChatModule {
    @Binds
    @Singleton
    abstract fun bindPaginatorRepository(imp: PaginatorRepositoryImpl): PaginatorRepository
}

@Module
@InstallIn(ViewModelComponent::class)
object FeatureChatViewModelModule {

    @Provides
    @ChatScopeId
    fun provideChatScopeId(): String = ScopeId.CHAT.name
}