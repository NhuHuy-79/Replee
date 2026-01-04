package com.nhuhuy.replee.core.firebase.repository

import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.error_handling.Failure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.safeCall
import com.nhuhuy.replee.core.firebase.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.firebase.data_source.AuthDataSource
import com.nhuhuy.replee.core.firebase.toAccount
import com.nhuhuy.replee.core.firebase.utils.toRemoteFailure
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface AccountRepository {
    suspend fun getCurrentAccount() : Resource<Account, Failure>
}

class AccountRepositoryImp @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val authDataSource: AuthDataSource,
    private val accountNetworkDataSource: AccountNetworkDataSource,
) : AccountRepository{
    override suspend fun getCurrentAccount(): Resource<Account, Failure> {
        return withContext(dispatcher){
            safeCall(
                throwable = { e -> e.toRemoteFailure() }
            ){
                val id = authDataSource.provideCurrentUser().uid
                accountNetworkDataSource.getAccountById(id).toAccount()
            }
        }
    }

}