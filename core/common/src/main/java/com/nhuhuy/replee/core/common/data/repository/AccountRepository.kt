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
import com.nhuhuy.replee.core.firebase.data_source.FirebaseAuthEmailService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

interface AccountRepository {
    suspend fun updateDeviceToken(token: String) : Resource<Unit, Failure>
    suspend fun getAccountById(uid: String) : Account
    suspend fun getCurrentAccount(): Account
    suspend fun searchAccountsByEmail(query: String): Resource<List<Account>, RemoteFailure>
    suspend fun updateBlockedUsers(otherUser: String): Resource<Unit, RemoteFailure>
    fun observeBlockStatus(owner: String, otherUser: String): Flow<Boolean>
}

class AccountRepositoryImp @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val firebaseAuthEmailService: FirebaseAuthEmailService,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val accountLocalDataSource: AccountLocalDataSource,
) : AccountRepository {
    override suspend fun updateDeviceToken(token: String): Resource<Unit, Failure> {
        return withContext(dispatcher){
            safeCall(
                throwable = { e -> e.toRemoteFailure()}
            ){
                val uid = firebaseAuthEmailService.getCurrentUser().uid
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
            val id = firebaseAuthEmailService.getCurrentUser().uid
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

    override suspend fun updateBlockedUsers(otherUser: String): Resource<Unit, RemoteFailure> {
        return withContext(dispatcher) {
            safeCall(
                throwable = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                }
            ) {
                val ownerId = firebaseAuthEmailService.getCurrentUser().uid
                val owner = accountLocalDataSource.getAccountWithId(ownerId)
                val newList = owner.blockedUserList + otherUser
                accountLocalDataSource.updateBlockedList(list = newList, owner = ownerId)
                accountNetworkDataSource.updateBlockedList(list = newList, owner = ownerId)
            }
        }
    }

    override fun observeBlockStatus(
        owner: String,
        otherUser: String
    ): Flow<Boolean> {
        return accountLocalDataSource.observeBlockStatus(owner)
            .map { list ->
                Timber.d("$list")
                list.contains(otherUser)
            }
    }
}