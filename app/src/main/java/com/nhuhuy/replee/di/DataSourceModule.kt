package com.nhuhuy.replee.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nhuhuy.replee.core.common.data.AccountDataSource
import com.nhuhuy.replee.feature_auth.data.source.AuthDataSource
import com.nhuhuy.replee.feature_auth.utils.Validator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideValidator() = Validator()

    @Provides
    @Singleton
    fun provideDispatcherIO() = Dispatchers.IO

    @Provides
    @Singleton
    fun provideAccountDataSource(firebaseAuth: FirebaseFirestore): AccountDataSource = AccountDataSource(firebaseAuth)

    @Provides
    @Singleton
    fun provideAuthDataSource(firebaseAuth: FirebaseAuth) : AuthDataSource = AuthDataSource(firebaseAuth)

}


