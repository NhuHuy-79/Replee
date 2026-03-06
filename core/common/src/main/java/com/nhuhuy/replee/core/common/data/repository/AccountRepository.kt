package com.nhuhuy.replee.core.common.data.repository

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.AccountRepository
import com.nhuhuy.replee.core.common.mapper.toAccount
import com.nhuhuy.replee.core.common.mapper.toAccountDTO
import com.nhuhuy.replee.core.common.mapper.toAccountEntity
import com.nhuhuy.replee.core.common.utils.ioExecute
import com.nhuhuy.replee.core.common.utils.ioExecuteWithTimeout
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.FirebaseAuthEmailService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AccountRepositoryImp @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val firebaseAuthEmailService: FirebaseAuthEmailService,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val accountLocalDataSource: AccountLocalDataSource,
) : AccountRepository {
    override suspend fun createAccount(account: Account): NetworkResult<Account> = ioExecute {
        val entity = account.toAccountEntity()
        val dto = account.toAccountDTO()

        accountLocalDataSource.upsertAccount(entity)

        accountNetworkDataSource.sendAccount(dto)

        account
    }

    override suspend fun updateDeviceToken(token: String): NetworkResult<Unit> {
        return ioExecute {
            val uid = firebaseAuthEmailService.getCurrentUser()?.uid ?: return@ioExecute
            accountNetworkDataSource.updateDeviceToken(uid, token)
        }
    }

    override suspend fun getAccountById(uid: String): Account {
        return withContext(dispatcher){
            accountLocalDataSource.getAccountWithId(uid)
                ?.toAccount() ?: Account()
        }
    }

    override suspend fun getCurrentAccount(): Account {
        return withContext(dispatcher) {
            val id = firebaseAuthEmailService.getCurrentUser()?.uid
            id?.let {
                accountLocalDataSource.getAccountWithId(uid = id)?.toAccount() ?: Account()
            } ?: return@withContext Account()
        }
    }

    override suspend fun searchAccountsByEmail(email: String): NetworkResult<List<Account>> {
        return ioExecuteWithTimeout {
            val accountDTOs = accountNetworkDataSource.fetchAccountsByEmail(email)
            val entities = accountDTOs.map { accountDTO -> accountDTO.toAccountEntity() }
            accountLocalDataSource.upsertAccounts(entities)
            accountDTOs.map { accountDTOs -> accountDTOs.toAccount() }
        }
    }

    override suspend fun updateBlockedUsers(otherUser: String): NetworkResult<Unit> {
        return ioExecuteWithTimeout {
            val ownerId =
                firebaseAuthEmailService.getCurrentUser()?.uid ?: return@ioExecuteWithTimeout
            val owner = accountLocalDataSource.getAccountWithId(ownerId)

            owner?.let {
                val newList = owner.blockedUserList + otherUser
                accountLocalDataSource.updateBlockedList(list = newList, owner = ownerId)
                accountNetworkDataSource.updateBlockedList(list = newList, owner = ownerId)
            }
        }
    }

    override suspend fun removeUserFromBlockedList(uid: String): NetworkResult<Unit> {
        return ioExecuteWithTimeout {
            val ownerId =
                firebaseAuthEmailService.getCurrentUser()?.uid ?: return@ioExecuteWithTimeout
            val owner = accountLocalDataSource.getAccountWithId(ownerId)

            owner?.let {
                val newList = owner.blockedUserList - uid
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

    override suspend fun isBlocked(owner: String, otherUser: String): Boolean {
        return try {
            var user = accountLocalDataSource.getAccountWithId(otherUser)?.toAccount()
            if (user == null) {
                user = accountNetworkDataSource.fetchAccountById(otherUser).toAccount()
            }
            user.blockedList.contains(owner)
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }
}