package com.nhuhuy.replee.feature_auth.data.repository

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.NetworkResultCaller
import com.nhuhuy.core.domain.utils.Logger
import com.nhuhuy.replee.core.common.data.preferences.AppPreferences
import com.nhuhuy.replee.core.common.mapper.toAccount
import com.nhuhuy.replee.core.common.mapper.toAccountEntity
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.firebase.data.AccountDTO
import com.nhuhuy.replee.core.firebase.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.firebase.data_source.FirebaseAuthEmailService
import com.nhuhuy.replee.core.firebase.data_source.GoogleAuthService
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    logger: Logger,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val googleAuthService: GoogleAuthService,
    private val firebaseAuthEmailService: FirebaseAuthEmailService,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val accountLocalDataSource: AccountLocalDataSource,
    private val appPreferences: AppPreferences
) : AuthRepository, NetworkResultCaller(dispatcher = ioDispatcher, logger = logger) {
    override suspend fun signInWithGoogle(idToken: String): NetworkResult<Account> {
        return safeCallWithTimeout {
            googleAuthService.signIn(idToken).toAccount()
        }
    }

    override suspend fun loginWithEmail(
        email: String, password: String
    ): NetworkResult<String> = safeCallWithTimeout {
        firebaseAuthEmailService.loginWithEmail(email, password)

        val userId = firebaseAuthEmailService.getCurrentUser().uid
        val account = accountNetworkDataSource.fetchAccountById(userId).toAccountEntity()
        accountLocalDataSource.upsertAccount(account.copy(logOut = false))

        val token = firebaseAuthEmailService.getDeviceToken()
        accountNetworkDataSource.updateDeviceToken(userId, token)

        appPreferences.saveLoggedStatus(true)
        userId
    }

    override suspend fun signUpWithEmail(
        name: String, email: String, password: String
    ): NetworkResult<Account> = safeCallWithTimeout {
        firebaseAuthEmailService.signUpWithEmail(email, password)
        val id = firebaseAuthEmailService.getCurrentUser().uid
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

        appPreferences.saveLoggedStatus(true)
        account.toAccount()
    }

    override suspend fun sendRecoverPasswordEmail(email: String): NetworkResult<Unit> =
        safeCallWithTimeout {
            firebaseAuthEmailService.sendRecoverPasswordEmail(email)
        }

    override suspend fun provideCurrentUser(): NetworkResult<String> = safeCallWithTimeout {
        firebaseAuthEmailService.getCurrentUser().uid
    }

    override fun isUserLogged(): Boolean {
        return appPreferences.getLoggedStatus()
    }
}