package com.nhuhuy.replee.di

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.network.KtorService
import com.nhuhuy.replee.core.network.network.KtorServiceImp
import com.nhuhuy.replee.feature_chat.data.NotifyService
import com.nhuhuy.replee.feature_chat.data.NotifyServiceImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule{
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
            level = LogLevel.ALL
        }
        install(ContentNegotiation){
            json(
                json = Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    @Provides
    @Singleton
    fun provideKtorService(client: HttpClient): KtorService = KtorServiceImp(client)

    @Provides
    @Singleton
    fun provideSendMessageService(
        logger: com.nhuhuy.core.domain.utils.Logger,
        ioDispatcher: CoroutineDispatcher,
        messaging: FirebaseMessaging,
        accountNetworkDataSource: AccountNetworkDataSource,
        accountLocalDataSource: AccountLocalDataSource,
        service: KtorServiceImp
    ): NotifyService = NotifyServiceImp(
        logger = logger,
        dispatcher = ioDispatcher,
        messaging = messaging,
        accountNetworkDataSource = accountNetworkDataSource,
        accountLocalDataSource = accountLocalDataSource,
        service = service,
    )
}