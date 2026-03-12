package com.nhuhuy.replee.di

import com.cloudinary.android.MediaManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.messaging
import com.nhuhuy.replee.core.network.api.KtorService
import com.nhuhuy.replee.core.network.api.KtorServiceImp
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
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModuleProvider {
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

