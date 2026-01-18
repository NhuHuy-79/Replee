package com.nhuhuy.replee.core.common.data.repository

import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.data.model.toAccount
import com.nhuhuy.replee.core.common.data.model.toAccountEntity
import com.nhuhuy.replee.core.common.error_handling.Failure
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.safeCall
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.firebase.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.firebase.data_source.FirebaseAuthService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface AccountRepository {
    suspend fun updateDeviceToken(token: String) : Resource<Unit, Failure>
    suspend fun getAccountById(uid: String) : Account
    suspend fun getCurrentAccount(): Account
    suspend fun searchAccountsByEmail(query: String): Resource<List<Account>, RemoteFailure>
}

class AccountRepositoryImp @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val firebaseAuthService: FirebaseAuthService,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val accountLocalDataSource: AccountLocalDataSource,
) : AccountRepository {
    override suspend fun updateDeviceToken(token: String): Resource<Unit, Failure> {
        return withContext(dispatcher){
            safeCall(
                throwable = { e -> e.toRemoteFailure()}
            ){
                val uid = firebaseAuthService.getCurrentUser().uid
                accountNetworkDataSource.updateDeviceToken(uid, token)
            }
        }
    }

    override suspend fun getAccountById(uid: String): Account {
        return withContext(dispatcher){
            accountLocalDataSource.getAccountWithId(uid)
                .toAccount()
        }
    }

    override suspend fun getCurrentAccount(): Account {
        return withContext(dispatcher) {
            val id = firebaseAuthService.getCurrentUser().uid
            accountLocalDataSource.getAccountWithId(uid = id).toAccount()
        }
    }

    override suspend fun searchAccountsByEmail(query: String): Resource<List<Account>, RemoteFailure> {
        return withContext(dispatcher) {
            safeCall(
                throwable = { e -> e.toRemoteFailure() }
            ) {
                val accountDTOs = accountNetworkDataSource.fetchAccountsByEmail(query)
                val entities = accountDTOs.map { accountDTO -> accountDTO.toAccountEntity() }
                accountLocalDataSource.upsertAccounts(entities)
                accountDTOs.map { accountDTOs -> accountDTOs.toAccount() }
            }
        }
    }
}