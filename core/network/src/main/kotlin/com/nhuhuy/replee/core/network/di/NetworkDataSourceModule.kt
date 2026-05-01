package com.nhuhuy.replee.core.network.di

import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSourceImp
import com.nhuhuy.replee.core.network.data_source.FirebasePresenceDataSource
import com.nhuhuy.replee.core.network.data_source.PresenceNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.PushNotificationNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.PushNotificationNetworkDataSourceImp
import com.nhuhuy.replee.core.network.data_source.UploadFileService
import com.nhuhuy.replee.core.network.imp.RetrofitUploader
import com.nhuhuy.replee.core.network.quailify.Retrofit
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindAccountNetworkDataSource(imp: AccountNetworkDataSourceImp): AccountNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindPresenceNetworkDataSource(imp: FirebasePresenceDataSource): PresenceNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindTokenNetworkDataSource(imp: PushNotificationNetworkDataSourceImp): PushNotificationNetworkDataSource

    @Binds
    @Singleton
    @Retrofit
    abstract fun bindRetrofitFileUploader(imp: RetrofitUploader): UploadFileService
}
