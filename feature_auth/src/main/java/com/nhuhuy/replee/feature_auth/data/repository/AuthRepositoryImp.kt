package com.nhuhuy.replee.feature_auth.data.repository

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.mapper.toAccount
import com.nhuhuy.replee.core.common.mapper.toAccountEntity
import com.nhuhuy.replee.core.common.utils.ioExecuteWithTimeout
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.AuthState
import com.nhuhuy.replee.core.network.data_source.FirebaseAuthEmailService
import com.nhuhuy.replee.core.network.data_source.GoogleAuthService
import com.nhuhuy.replee.core.network.model.AccountDTO
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val googleAuthService: GoogleAuthService,
    private val firebaseAuthEmailService: FirebaseAuthEmailService,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val accountLocalDataSource: AccountLocalDataSource
) : AuthRepository {
    override suspend fun signInWithGoogle(idToken: String): NetworkResult<Account> {
        return ioExecuteWithTimeout {
            val accountDTO = googleAuthService.signIn(idToken)
            accountDTO.toAccount()
        }
    }

    override suspend fun loginWithEmail(
        email: String, password: String
    ): NetworkResult<String> = ioExecuteWithTimeout {
        firebaseAuthEmailService.loginWithEmail(email, password)

        val userId =
            firebaseAuthEmailService.getCurrentUser()?.uid ?: return@ioExecuteWithTimeout ""
        val account = accountNetworkDataSource.fetchAccountById(userId).toAccountEntity()
        accountLocalDataSource.upsertAccount(account.copy(logOut = false))

        val token = firebaseAuthEmailService.getDeviceToken()
        accountNetworkDataSource.updateDeviceToken(userId, token)

        userId
    }

    override suspend fun signUpWithEmail(
        name: String, email: String, password: String
    ): NetworkResult<Account> = ioExecuteWithTimeout {
        firebaseAuthEmailService.signUpWithEmail(email, password)
        val id =
            firebaseAuthEmailService.getCurrentUser()?.uid ?: return@ioExecuteWithTimeout Account()
        val account = AccountDTO(
            id = id,
            name = name,
            email = email,
        )
        try {
            accountNetworkDataSource.sendAccount(account)
            accountLocalDataSource.upsertAccount(account.toAccountEntity().copy(logOut = false))

            val token = firebaseAuthEmailService.getDeviceToken()
            accountNetworkDataSource.updateDeviceToken(id, token)

        } catch (e: Exception) {
            Timber.e(e)
            firebaseAuthEmailService.deleteCurrentUser()
        }

        account.toAccount()
    }

    override suspend fun sendRecoverPasswordEmail(email: String): NetworkResult<Unit> =
        ioExecuteWithTimeout {
            firebaseAuthEmailService.sendRecoverPasswordEmail(email)
        }

    override fun observeAuthState(): Flow<String?> {
        return firebaseAuthEmailService.observeAuthState()
    }

    override fun observeAuthenticationState(): Flow<AuthState> {
        return firebaseAuthEmailService.authState()
    }
}