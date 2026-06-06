package com.nhuhuy.replee.core.network.di

import com.nhuhuy.replee.core.network.api.mapper.RequestMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {

    @Provides
    @Singleton
    fun provideNetworkMapper(json: Json) = RequestMapper(json)
}
