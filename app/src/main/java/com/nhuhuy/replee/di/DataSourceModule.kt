package com.nhuhuy.replee.di

import com.nhuhuy.replee.core.data.data_source.file_path.FilePathLocalDataSource
import com.nhuhuy.replee.core.data.data_source.file_path.FilePathLocalDataSourceImp
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSourceImp
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSourceImp
import com.nhuhuy.replee.core.network.data_source.FirebasePresenceDataSource
import com.nhuhuy.replee.core.network.data_source.PresenceNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.PushNotificationNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.PushNotificationNetworkDataSourceImp
import com.nhuhuy.replee.core.network.data_source.UploadFileService
import com.nhuhuy.replee.core.network.imp.RetrofitUploader
import com.nhuhuy.replee.core.network.quailify.Retrofit
import com.nhuhuy.replee.feature_auth.data.data_source.AuthNetworkDataSource
import com.nhuhuy.replee.feature_auth.data.data_source.AuthNetworkDataSourceImp
import com.nhuhuy.replee.feature_chat.data.NotificationHelper
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSourceImp
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSourceImp
import com.nhuhuy.replee.feature_chat.data.source.message.MessageActionDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageActionDataSourceImp
import com.nhuhuy.replee.feature_chat.data.source.message.MessageLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageLocalDataSourceImp
import com.nhuhuy.replee.feature_chat.data.source.message.MessageNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageNetworkDataSourceImp
import com.nhuhuy.replee.feature_chat.data.source.metadata.MetaDataNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.metadata.MetaDataNetworkDataSourceImpl
import com.nhuhuy.replee.feature_chat.data.source.paging.PagingMessageNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.paging.PagingMessageNetworkDataSourceImp
import com.nhuhuy.replee.feature_profile.data.source.ProfileLocalDataSource
import com.nhuhuy.replee.feature_profile.data.source.ProfileLocalDataSourceImp
import com.nhuhuy.replee.feature_profile.data.source.ProfileNetworkDataSource
import com.nhuhuy.replee.feature_profile.data.source.ProfileNetworkDataSourceImp
import com.nhuhuy.replee.helper.NotificationHelperImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModuleBinder {
    //Chat Module
    @Binds
    abstract fun bindMessageActionDataSource(
        imp: MessageActionDataSourceImp
    ): MessageActionDataSource
    @Binds
    abstract fun bindPresenceNetworkDataSource(
        imp: FirebasePresenceDataSource
    ): PresenceNetworkDataSource

    @Binds
    abstract fun bindMetaDataNetworkDataSource(
        imp: MetaDataNetworkDataSourceImpl
    ): MetaDataNetworkDataSource

    @Binds
    abstract fun bindMessageLocalDataSource(
        imp: MessageLocalDataSourceImp
    ): MessageLocalDataSource

    @Binds
    abstract fun bindMessageNetworkDataSource(
        imp: MessageNetworkDataSourceImp
    ): MessageNetworkDataSource

    @Binds
    abstract fun bindConversationLocalDataSource(
        imp: ConversationLocalDataSourceImp
    ): ConversationLocalDataSource

    @Binds
    abstract fun bindConversationNetworkDataSource(
        imp: ConversationNetworkDataSourceImp
    ): ConversationNetworkDataSource

    @Binds
    abstract fun bindFilePathLocalDataSource(
        imp: FilePathLocalDataSourceImp
    ): FilePathLocalDataSource

    @Binds
    abstract fun bindProfileLocalDataSource(imp: ProfileLocalDataSourceImp): ProfileLocalDataSource

    @Binds
    abstract fun bindProfileNetworkDataSource(imp: ProfileNetworkDataSourceImp): ProfileNetworkDataSource

    @Binds
    @Retrofit
    abstract fun bindRetrofitFileUploader(imp: RetrofitUploader): UploadFileService

    @Binds
    abstract fun bindAuthNetworkDataSource(imp: AuthNetworkDataSourceImp): AuthNetworkDataSource

    @Binds
    abstract fun bindTokenNetworkDataSource(imp: PushNotificationNetworkDataSourceImp): PushNotificationNetworkDataSource

    @Binds
    abstract fun bindAccountLocalDataSource(imp: AccountLocalDataSourceImp): AccountLocalDataSource

    @Binds
    abstract fun bindAccountNetworkDataSource(imp: AccountNetworkDataSourceImp): AccountNetworkDataSource

    @Binds
    abstract fun bindNotificationHelper(imp: NotificationHelperImp): NotificationHelper

    @Binds
    abstract fun bindPageMessageNetworkDataSource(imp: PagingMessageNetworkDataSourceImp): PagingMessageNetworkDataSource
}
