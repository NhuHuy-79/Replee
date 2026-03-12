package com.nhuhuy.replee.feature_auth.data.repository

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.mapper.toAccount
import com.nhuhuy.replee.core.common.mapper.toAccountEntity
import com.nhuhuy.replee.core.common.utils.executeWithTimeout
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.AuthenticatedState
import com.nhuhuy.replee.core.network.data_source.FirebaseAuthEmailService
import com.nhuhuy.replee.feature_auth.data.data_source.AuthNetworkDataSource
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val authNetworkDataSource: AuthNetworkDataSource,
    private val ioDispatcher: CoroutineDispatcher,
    private val firebaseAuthEmailService: FirebaseAuthEmailService,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val accountLocalDataSource: AccountLocalDataSource
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
        val account = accountNetworkDataSource.fetchAccountById(userId).toAccountEntity()
        accountLocalDataSource.upsertAccount(account.copy(logOut = false))

        val token = firebaseAuthEmailService.getDeviceToken()
        accountNetworkDataSource.updateDeviceToken(userId, token)

        userId
    }

    override suspend fun signUpWithEmail(
        name: String, email: String, password: String
    ): NetworkResult<Account> = executeWithTimeout(dispatcher = ioDispatcher) {
        val account = authNetworkDataSource.signUpWithEmail(name = name, email, password)
        try {
            accountNetworkDataSource.sendAccount(account)
            accountLocalDataSource.upsertAccount(account.toAccountEntity().copy(logOut = false))

            val token = firebaseAuthEmailService.getDeviceToken()
            accountNetworkDataSource.updateDeviceToken(account.id, token)

        } catch (e: Exception) {
            Timber.e(e)
            firebaseAuthEmailService.deleteCurrentUser()
        }

        account.toAccount()
    }

    override suspend fun sendRecoverPasswordEmail(email: String): NetworkResult<Unit> =
        executeWithTimeout(dispatcher = ioDispatcher) {
            authNetworkDataSource.sendRecoverPasswordEmail(email)
        }

    override fun observeAuthState(): Flow<String?> {
        return firebaseAuthEmailService.observeAuthState()
    }

    override fun observeAuthenticationState(): Flow<AuthenticatedState> {
        return authNetworkDataSource.observeAuthenticatedState()
    }
}