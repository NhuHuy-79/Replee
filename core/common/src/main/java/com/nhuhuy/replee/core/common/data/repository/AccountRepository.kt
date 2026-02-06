package com.nhuhuy.replee.core.common.data.repository

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.AccountRepository
import com.nhuhuy.core.domain.repository.NetworkResultCaller
import com.nhuhuy.core.domain.utils.Logger
import com.nhuhuy.replee.core.common.mapper.toAccount
import com.nhuhuy.replee.core.common.mapper.toAccountDTO
import com.nhuhuy.replee.core.common.mapper.toAccountEntity
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.firebase.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.firebase.data_source.FirebaseAuthEmailService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AccountRepositoryImp @Inject constructor(
    private val logger: Logger,
    private val dispatcher: CoroutineDispatcher,
    private val firebaseAuthEmailService: FirebaseAuthEmailService,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val accountLocalDataSource: AccountLocalDataSource,
) : AccountRepository,
    NetworkResultCaller(dispatcher, logger) {
    override suspend fun createAccount(account: Account): NetworkResult<Account> = safeCall {
        val entity = account.toAccountEntity()
        val dto = account.toAccountDTO()

        accountLocalDataSource.upsertAccount(entity)

        accountNetworkDataSource.sendAccount(dto)

        account
    }

    override suspend fun updateDeviceToken(token: String): NetworkResult<Unit> {
        return safeCall {
            val uid = firebaseAuthEmailService.getCurrentUser().uid
            accountNetworkDataSource.updateDeviceToken(uid, token)
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

    override suspend fun searchAccountsByEmail(email: String): NetworkResult<List<Account>> {
        return safeCallWithTimeout {
            val accountDTOs = accountNetworkDataSource.fetchAccountsByEmail(email)
            val entities = accountDTOs.map { accountDTO -> accountDTO.toAccountEntity() }
            accountLocalDataSource.upsertAccounts(entities)
            accountDTOs.map { accountDTOs -> accountDTOs.toAccount() }
        }
    }

    override suspend fun updateBlockedUsers(otherUser: String): NetworkResult<Unit> {
        return safeCallWithTimeout {
            val ownerId = firebaseAuthEmailService.getCurrentUser().uid
            val owner = accountLocalDataSource.getAccountWithId(ownerId)
            val newList = owner.blockedUserList + otherUser
            accountLocalDataSource.updateBlockedList(list = newList, owner = ownerId)
            accountNetworkDataSource.updateBlockedList(list = newList, owner = ownerId)
        }
    }

    override suspend fun removeUserFromBlockedList(uid: String): NetworkResult<Unit> {
        return safeCallWithTimeout {
            val ownerId = firebaseAuthEmailService.getCurrentUser().uid
            val owner = accountLocalDataSource.getAccountWithId(ownerId)
            val newList = owner.blockedUserList - uid
            accountLocalDataSource.updateBlockedList(list = newList, owner = ownerId)
            accountNetworkDataSource.updateBlockedList(list = newList, owner = ownerId)
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