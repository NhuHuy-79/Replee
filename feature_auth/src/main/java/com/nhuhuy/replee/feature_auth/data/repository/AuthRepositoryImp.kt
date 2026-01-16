package com.nhuhuy.replee.feature_auth.data.repository

import com.nhuhuy.replee.core.common.data.model.toAccountEntity
import com.nhuhuy.replee.core.common.data.preferences.AppPreferences
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.safeCall
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.firebase.data.AccountDTO
import com.nhuhuy.replee.core.firebase.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.firebase.data_source.FirebaseAuthService
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val firebaseAuthService: FirebaseAuthService,
    private val dispatcher: CoroutineDispatcher,
    private val accountLocalDataSource: AccountLocalDataSource,
    private val appPreferences: AppPreferences
    ) : AuthRepository {
    override suspend fun loginWithEmail(
        email: String,
        password: String
    ): Resource<String, RemoteFailure> {
        return withContext(dispatcher) {
            safeCall(
                throwable = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                }
            ) {
                firebaseAuthService.loginWithEmail(email, password)

                val userId = firebaseAuthService.provideCurrentUser().uid
                val account = accountNetworkDataSource.getAccountById(userId).toAccountEntity()
                accountLocalDataSource.saveAccount(account.copy(logOut = false))

                val token = firebaseAuthService.provideToken()
                accountNetworkDataSource.updateNewToken(userId,token)

                appPreferences.setLoggedStatus(true)
                userId
            }
        }
    }

    override suspend fun signUpWithEmail(
        name: String,
        email: String,
        password: String
    ): Resource<String, RemoteFailure> {
        return withContext(dispatcher) {
            safeCall(
                throwable = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                },
            ) {
                firebaseAuthService.signUpWithEmail(email, password)

                val id = firebaseAuthService.provideCurrentUser().uid
                try {
                    val account = AccountDTO(
                        id = id,
                        name = name,
                        email = email,
                    )
                    accountNetworkDataSource.addAccount(account)
                    accountLocalDataSource.saveAccount(account.toAccountEntity())
                } catch (e: Exception) {
                    Timber.e(e)
                    firebaseAuthService.deleteCurrentUser()
                }

                appPreferences.setLoggedStatus(true)
                id
            }
        }
    }

    override suspend fun sendRecoverPasswordEmail(email: String): Resource<Unit, RemoteFailure> {
        return withContext(dispatcher) {
            safeCall(
                throwable = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                }
            ) {
                firebaseAuthService.sendRecoverPasswordEmail(email)
            }
        }
    }

    override suspend fun provideCurrentUser(): Resource<String, RemoteFailure> {
        return safeCall(
            throwable = { e ->
                Timber.e(e)
                e.toRemoteFailure()
            }
        ) {
            firebaseAuthService.provideCurrentUser().uid
        }
    }

    override fun isUserLogged(): Boolean {
        return appPreferences.getLoggedStatus()
    }
}