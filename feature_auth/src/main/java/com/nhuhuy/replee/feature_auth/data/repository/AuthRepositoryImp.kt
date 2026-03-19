package com.nhuhuy.replee.feature_auth.data.repository

import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.AuthenticatedState
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.mapper.toAccount
import com.nhuhuy.replee.core.common.utils.execute
import com.nhuhuy.replee.core.common.utils.executeWithTimeout
import com.nhuhuy.replee.feature_auth.data.data_source.AuthNetworkDataSource
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val sessionManager: SessionManager,
    private val authNetworkDataSource: AuthNetworkDataSource,
    private val ioDispatcher: CoroutineDispatcher
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

    override fun observeAuthState(): Flow<String?> {
        return sessionManager.userIdState
    }

    override fun observeAuthenticationState(): Flow<AuthenticatedState> {
        return sessionManager.authenticatedState
    }
}