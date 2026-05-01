package com.nhuhuy.replee.feature_profile.di

import com.nhuhuy.replee.feature_profile.data.repository.ProfileRepositoryImp
import com.nhuhuy.replee.feature_profile.data.source.ProfileLocalDataSource
import com.nhuhuy.replee.feature_profile.data.source.ProfileLocalDataSourceImp
import com.nhuhuy.replee.feature_profile.data.source.ProfileNetworkDataSource
import com.nhuhuy.replee.feature_profile.data.source.ProfileNetworkDataSourceImp
import com.nhuhuy.replee.feature_profile.data.worker.ProfileScheduler
import com.nhuhuy.replee.feature_profile.data.worker.ProfileSchedulerImp
import com.nhuhuy.replee.feature_profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {

    @Binds
    @Singleton
    abstract fun bindProfileRepository(profileRepositoryImp: ProfileRepositoryImp): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindProfileLocalDataSource(imp: ProfileLocalDataSourceImp): ProfileLocalDataSource

    @Binds
    @Singleton
    abstract fun bindProfileNetworkDataSource(imp: ProfileNetworkDataSourceImp): ProfileNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindProfileSchedular(imp: ProfileSchedulerImp): ProfileScheduler
}
