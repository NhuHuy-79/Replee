package com.nhuhuy.replee.core.common.repository

import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.data.model.toAccount
import com.nhuhuy.replee.core.common.error_handling.Failure
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.safeCall
import com.nhuhuy.replee.core.firebase.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.firebase.data_source.AuthDataSource
import com.nhuhuy.replee.core.common.toRemoteFailure
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface AccountRepository {
    suspend fun getAccountById(uid: String) : Resource<Account, Failure>
    suspend fun getCurrentAccount(): Resource<Account, Failure>
    suspend fun getAccountListWithEmail(query: String): Resource<List<Account>, RemoteFailure>
}

class AccountRepositoryImp @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val authDataSource: AuthDataSource,
    private val accountNetworkDataSource: AccountNetworkDataSource,
) : AccountRepository {
    override suspend fun getAccountById(uid: String): Resource<Account, Failure> {
        return withContext(dispatcher){
            safeCall(
                throwable = { e -> e.toRemoteFailure() }
            ){
                accountNetworkDataSource.getAccountById(uid).toAccount()
            }

        }
    }

    override suspend fun getCurrentAccount(): Resource<Account, Failure> {
        return withContext(dispatcher) {
            safeCall(
                throwable = { e -> e.toRemoteFailure() }
            ) {
                val id = authDataSource.provideCurrentUser().uid
                accountNetworkDataSource.getAccountById(id).toAccount()
            }
        }
    }

    override suspend fun getAccountListWithEmail(query: String): Resource<List<Account>, RemoteFailure> {
        return withContext(dispatcher) {
            safeCall(
                throwable = { e -> e.toRemoteFailure() }
            ) {
                accountNetworkDataSource.searchUserByEmail(query).map { accountDTO ->
                    accountDTO.toAccount()
                }
            }
        }
    }

}