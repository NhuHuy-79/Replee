package com.nhuhuy.replee.core.network.di

import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSourceImp
import com.nhuhuy.replee.core.network.data_source.AuthNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.AuthNetworkDataSourceImp
import com.nhuhuy.replee.core.network.data_source.ConversationNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.ConversationNetworkDataSourceImp
import com.nhuhuy.replee.core.network.data_source.FirebasePresenceDataSource
import com.nhuhuy.replee.core.network.data_source.MessageNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.MessageNetworkDataSourceImp
import com.nhuhuy.replee.core.network.data_source.MetaDataNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.MetaDataNetworkDataSourceImpl
import com.nhuhuy.replee.core.network.data_source.PagingMessageNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.PagingMessageNetworkDataSourceImp
import com.nhuhuy.replee.core.network.data_source.PresenceNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.ProfileNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.ProfileNetworkDataSourceImp
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
    abstract fun bindAuthNetworkDataSource(imp: AuthNetworkDataSourceImp): AuthNetworkDataSource

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

    @Binds
    @Singleton
    abstract fun bindMetaDataNetworkDataSource(imp: MetaDataNetworkDataSourceImpl): MetaDataNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindMessageNetworkDataSource(imp: MessageNetworkDataSourceImp): MessageNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindConversationNetworkDataSource(imp: ConversationNetworkDataSourceImp): ConversationNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindPageMessageNetworkDataSource(imp: PagingMessageNetworkDataSourceImp): PagingMessageNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindProfileNetworkDataSource(imp: ProfileNetworkDataSourceImp): ProfileNetworkDataSource
}
