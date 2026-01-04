package com.nhuhuy.replee.feature_auth.data.repository

import com.nhuhuy.replee.core.firebase.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.safeCall
import com.nhuhuy.replee.core.firebase.utils.toRemoteFailure
import com.nhuhuy.replee.core.firebase.AccountDTO
import com.nhuhuy.replee.core.firebase.data_source.AuthDataSource
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val authDataSource: AuthDataSource,
    private val dispatcher: CoroutineDispatcher,
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
                authDataSource.loginWithEmail(email, password)
                authDataSource.provideCurrentUser().uid
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
                authDataSource.signUpWithEmail(email, password)
                val id = authDataSource.provideCurrentUser().uid
                try {
                    val account = AccountDTO(
                        id = id,
                        name = name,
                        email = email,
                    )
                    accountNetworkDataSource.addAccount(account)
                } catch (e: Exception) {
                    Timber.e(e)
                    authDataSource.deleteCurrentUser()
                }

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
                authDataSource.sendRecoverPasswordEmail(email)
            }
        }
    }

    override suspend fun provideCurrentUser(): Resource<String, RemoteFailure> {
        return safeCall(
            throwable = { e ->
                Timber.e(e)
                e.toRemoteFailure()
            }
        ){
            authDataSource.provideCurrentUser().uid
        }
    }

    override fun isUserLogged(): Boolean {
        return authDataSource.isUserLogged()
    }
}