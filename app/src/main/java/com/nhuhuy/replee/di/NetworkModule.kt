package com.nhuhuy.replee.di

import com.cloudinary.android.MediaManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.messaging
import com.nhuhuy.replee.core.network.api.KtorService
import com.nhuhuy.replee.core.network.api.KtorServiceImp
import com.nhuhuy.replee.core.network.api.cloudinary.CloudinaryApi
import com.nhuhuy.replee.core.network.api.fcm.FCM_URL
import com.nhuhuy.replee.core.network.api.fcm.FcmApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModuleProvider {

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
    fun provideFcmApi(retrofit: Retrofit): FcmApi {
        return retrofit.create(FcmApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCloudinaryRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.cloudinary.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }


    @Provides
    @Singleton
    fun provideCloudinaryApi(retrofit: Retrofit): CloudinaryApi {
        return retrofit.create(CloudinaryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMediaManager(): MediaManager {
        return MediaManager.get()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth() = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseFirestore() = Firebase.firestore

    @Provides
    @Singleton
    fun provideFirebaseDatabase() = Firebase.database

    @Provides
    @Singleton
    fun provideFirebaseMessaging() = Firebase.messaging

    @Provides
    @Singleton
    fun provideKtorClient() = HttpClient(OkHttp){
        install(Logging){
            logger = object : Logger {
                override fun log(message: String) {
                    Timber.tag("KTOR").d(message)
                }
            }
            level = LogLevel.ALL
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 120000
            connectTimeoutMillis = 60000
            socketTimeoutMillis = 120000
        }
        install(ContentNegotiation){
            json(
                json = Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModuleBinder {
    @Binds
    abstract fun bindKtorService(impl: KtorServiceImp): KtorService
}

