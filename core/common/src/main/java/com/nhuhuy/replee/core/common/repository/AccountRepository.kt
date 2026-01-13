package com.nhuhuy.replee.core.common.repository

import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.data.model.toAccount
import com.nhuhuy.replee.core.common.data.model.toAccountEntity
import com.nhuhuy.replee.core.common.error_handling.Failure
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.safeCall
import com.nhuhuy.replee.core.firebase.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.firebase.data_source.FirebaseAuthService
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface AccountRepository {
    suspend fun getAccountById(uid: String) : Account
    suspend fun getCurrentAccount(): Resource<Account, Failure>
    suspend fun getAccountListWithEmail(query: String): Resource<List<Account>, RemoteFailure>
}

class AccountRepositoryImp @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val firebaseAuthService: FirebaseAuthService,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val accountLocalDataSource: AccountLocalDataSource,
) : AccountRepository {
    override suspend fun getAccountById(uid: String): Account {
        return withContext(dispatcher){
            accountLocalDataSource.getAccountWithId(uid)
                .toAccount()
        }
    }

    override suspend fun getCurrentAccount(): Resource<Account, Failure> {
        return withContext(dispatcher) {
            safeCall(
                throwable = { e -> e.toRemoteFailure() }
            ) {
                val id = firebaseAuthService.provideCurrentUser().uid
                accountLocalDataSource.getAccountWithId(uid = id).toAccount()
            }
        }
    }

    override suspend fun getAccountListWithEmail(query: String): Resource<List<Account>, RemoteFailure> {
        return withContext(dispatcher) {
            safeCall(
                throwable = { e -> e.toRemoteFailure() }
            ) {
                val accountDTOs = accountNetworkDataSource.searchUserByEmail(query)
                val entities = accountDTOs.map { accountDTO -> accountDTO.toAccountEntity() }
                accountLocalDataSource.saveAccountList(entities)
                accountDTOs.map { accountDTOs -> accountDTOs.toAccount() }
            }
        }
    }
}