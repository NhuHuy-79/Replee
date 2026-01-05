package com.nhuhuy.replee.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.database.entity.account.AccountDao
import com.nhuhuy.replee.core.firebase.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.firebase.data_source.AuthDataSource
import com.nhuhuy.replee.feature_chat.data.mapper.ConversationMapper
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationRemoteDataSource
import com.nhuhuy.replee.feature_profile.data.data_store.SettingDataStore
import com.nhuhuy.replee.feature_profile.data.data_store.SettingDataStoreImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {
    @Provides
    @Singleton
    fun provideConversationMapper() = ConversationMapper()

    @Provides
    @Singleton
    fun provideAccountDataSource(firestore: FirebaseFirestore): AccountNetworkDataSource = AccountNetworkDataSource(firestore = firestore)

    @Singleton
    @Provides
    fun provideAccountLocalDataSource(dao: AccountDao) : AccountLocalDataSource =
        AccountLocalDataSource(dao)

    @Provides
    @Singleton
    fun provideAuthDataSource(firebaseAuth: FirebaseAuth) : AuthDataSource = AuthDataSource(firebaseAuth)

    @Provides
    @Singleton
    fun provideConversationDataSource(firestore: FirebaseFirestore) : ConversationRemoteDataSource =
        ConversationRemoteDataSource(firestore)

    @Provides
    @Singleton
    fun provideSettingDataStore(@ApplicationContext context: Context) : SettingDataStore =
        SettingDataStoreImp(context)
}


