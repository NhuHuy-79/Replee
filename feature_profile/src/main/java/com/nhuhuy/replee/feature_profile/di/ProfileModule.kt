package com.nhuhuy.replee.feature_profile.di

import android.content.Context
import com.nhuhuy.replee.feature_profile.data.data_store.SettingDataStore
import com.nhuhuy.replee.feature_profile.data.data_store.SettingDataStoreImp
import com.nhuhuy.replee.feature_profile.data.repository.ProfileRepositoryImp
import com.nhuhuy.replee.feature_profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModuleBinder {
    @Binds
    abstract fun bindProfileRepository(profileRepositoryImp: ProfileRepositoryImp): ProfileRepository

}

@Module
@InstallIn(SingletonComponent::class)
object ProfileModuleProvider {
    @Provides
    @Singleton
    fun provideSettingDataStore(@ApplicationContext context: Context): SettingDataStore =
        SettingDataStoreImp(context)
}