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
import com.nhuhuy.replee.core.firebase.data_source.FirebaseAuthEmailService
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val firebaseAuthEmailService: FirebaseAuthEmailService,
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
                firebaseAuthEmailService.loginWithEmail(email, password)

                val userId = firebaseAuthEmailService.getCurrentUser().uid
                val account = accountNetworkDataSource.fetchAccountById(userId).toAccountEntity()
                accountLocalDataSource.upsertAccount(account.copy(logOut = false))

                val token = firebaseAuthEmailService.getDeviceToken()
                accountNetworkDataSource.updateDeviceToken(userId,token)

                appPreferences.saveLoggedStatus(true)
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
                firebaseAuthEmailService.signUpWithEmail(email, password)

                val id = firebaseAuthEmailService.getCurrentUser().uid
                try {
                    val account = AccountDTO(
                        id = id,
                        name = name,
                        email = email,
                    )
                    accountNetworkDataSource.sendAccount(account)
                    accountLocalDataSource.upsertAccount(account.toAccountEntity().copy(logOut = false))

                    val token = firebaseAuthEmailService.getDeviceToken()
                    accountNetworkDataSource.updateDeviceToken(id,token)

                } catch (e: Exception) {
                    Timber.e(e)
                    firebaseAuthEmailService.deleteCurrentUser()
                }

                appPreferences.saveLoggedStatus(true)
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
                firebaseAuthEmailService.sendRecoverPasswordEmail(email)
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
            firebaseAuthEmailService.getCurrentUser().uid
        }
    }

    override fun isUserLogged(): Boolean {
        return appPreferences.getLoggedStatus()
    }
}