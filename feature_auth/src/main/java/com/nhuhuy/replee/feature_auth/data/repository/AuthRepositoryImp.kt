package com.nhuhuy.replee.feature_auth.data.repository

import com.nhuhuy.replee.core.common.data.AccountDataSource
import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.safeCall
import com.nhuhuy.replee.core.common.error_handling.toRemoteFailure
import com.nhuhuy.replee.feature_auth.data.source.AuthDataSource
import com.nhuhuy.replee.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val accountDataSource: AccountDataSource,
    private val authDataSource: AuthDataSource,
    private val dispatcher: CoroutineDispatcher,
) : AuthRepository {
    override suspend fun loginWithEmail(
        email: String,
        password: String
    ): Resource<String, RemoteFailure> {
        return withContext(dispatcher) {
            safeCall(
                errorMapper = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                }
            ) {
                authDataSource.loginWithEmail(email, password)
                authDataSource.currentUser.uid
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
                errorMapper = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                },
            ) {
                authDataSource.signUpWithEmail(email, password)
                val id = authDataSource.currentUser.uid
                try {
                    val account = Account(
                        id = id,
                        name = name,
                        email = email,
                    )
                    accountDataSource.addAccount(account)
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
                errorMapper = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                }
            ) {
                authDataSource.sendRecoverPasswordEmail(email)
            }
        }
    }
}