package com.nhuhuy.replee.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.messaging
import com.nhuhuy.replee.core.network.api.cloudinary.CloudinaryApi
import com.nhuhuy.replee.core.network.api.fcm.FCM_URL
import com.nhuhuy.replee.core.network.api.fcm.FcmApi
import com.nhuhuy.replee.core.network.quailify.CloudinaryUrl
import com.nhuhuy.replee.core.network.quailify.FcmBackendUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModuleProvider {

    @Provides
    @Singleton
    fun provideCredentialManager(
        @ApplicationContext context: Context
    ): CredentialManager {
        return CredentialManager.create(context)
    }
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @FcmBackendUrl
    @Singleton
    fun provideFcmRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(FCM_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideFcmApi(@FcmBackendUrl retrofit: Retrofit): FcmApi {
        return retrofit.create(FcmApi::class.java)
    }

    @Provides
    @CloudinaryUrl
    @Singleton
    fun provideCloudinaryRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.cloudinary.com/v1_1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }


    @Provides
    @Singleton
    fun provideCloudinaryApi(@CloudinaryUrl retrofit: Retrofit): CloudinaryApi {
        return retrofit.create(CloudinaryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth() = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseFirestore() = Firebase.firestore

    @Provides
    @Singleton
    fun provideDatabaseReference() = Firebase.database.reference

    @Provides
    @Singleton
    fun provideFirebaseMessaging() = Firebase.messaging

}

