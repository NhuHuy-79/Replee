package com.nhuhuy.replee.feature_auth.data.repository

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.data.mapper.toAccount
import com.nhuhuy.replee.core.data.utils.IoDispatcher

import com.nhuhuy.replee.core.data.utils.execute
import com.nhuhuy.replee.core.data.utils.executeWithTimeout
import com.nhuhuy.replee.feature_auth.data.data_source.AuthNetworkDataSource
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val authNetworkDataSource: AuthNetworkDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AuthRepository {
    override suspend fun signInWithGoogle(idToken: String): NetworkResult<Account> {
        return executeWithTimeout(dispatcher = ioDispatcher) {
            val accountDTO = authNetworkDataSource.signInWithGoogle(idToken)
            accountDTO.toAccount()
        }
    }

    override suspend fun loginWithEmail(
        email: String, password: String
    ): NetworkResult<String> = executeWithTimeout(dispatcher = ioDispatcher) {
        val userId = authNetworkDataSource.signInWithEmail(email, password)
        userId
    }

    override suspend fun signUpWithEmail(
        name: String, email: String, password: String
    ): NetworkResult<Account> = executeWithTimeout(dispatcher = ioDispatcher) {
        val account = authNetworkDataSource.signUpWithEmail(name = name, email, password)
        account.toAccount()
    }

    override suspend fun sendRecoverPasswordEmail(email: String): NetworkResult<Unit> =
        executeWithTimeout(dispatcher = ioDispatcher) {
            authNetworkDataSource.sendRecoverPasswordEmail(email)
        }

    override suspend fun provideAuthenticateToken(): NetworkResult<String> {
        return execute(dispatcher = ioDispatcher) {
            authNetworkDataSource.getCurrentAuthToken()
        }
    }
}