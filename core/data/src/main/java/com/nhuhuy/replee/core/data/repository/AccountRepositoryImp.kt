package com.nhuhuy.replee.core.data.repository

import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.SearchHistoryResult
import com.nhuhuy.core.domain.repository.AccountRepository
import com.nhuhuy.replee.core.data.mapper.toAccount
import com.nhuhuy.replee.core.data.mapper.toAccountDTO
import com.nhuhuy.replee.core.data.mapper.toAccountEntity
import com.nhuhuy.replee.core.data.utils.execute
import com.nhuhuy.replee.core.data.utils.executeWithTimeout
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.database.entity.search_history.SearchHistoryEntity
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.model.AccountDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AccountRepositoryImp @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val sessionManager: SessionManager,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val accountLocalDataSource: AccountLocalDataSource,
) : AccountRepository {
    override suspend fun createAccount(
        token: String,
        account: Account
    ): NetworkResult<Account> = execute(dispatcher = ioDispatcher) {
        val entity = account.toAccountEntity()
        val dto = account.toAccountDTO().copy(currentToken = token)

        accountLocalDataSource.upsertAccount(entity)
        accountNetworkDataSource.sendAccount(dto)
        account
    }

    override suspend fun fetchAccount(uid: String): NetworkResult<String> {
        return execute(ioDispatcher) {
            val accountDTO = accountNetworkDataSource.fetchAccountById(uid)
            val accountEntity = accountDTO.toAccountEntity()
            accountLocalDataSource.upsertAccount(accountEntity)
            uid
        }
    }

    override suspend fun fetchAccountList(uids: List<String>): NetworkResult<Unit> {
        return execute(ioDispatcher) {
            val accountDTOs: List<AccountDTO> = accountNetworkDataSource.fetchAccountByIdList(uids)
            val entities = accountDTOs.map { accountDTO -> accountDTO.toAccountEntity() }

            accountLocalDataSource.upsertAccounts(entities)
        }
    }

    override suspend fun updateDeviceToken(token: String): NetworkResult<Unit> {
        return execute {
            val uid = sessionManager.getUserIdOrNull()

            if (uid != null) {
                accountLocalDataSource.updateDeviceToken(uid = uid, token = token)
                accountNetworkDataSource.updateDeviceToken(uid, token)
            }
            Timber.Forest.e("Uid is nullable, need to work manager!")
        }
    }

    override suspend fun getAccountById(uid: String): Account {
        return withContext(ioDispatcher) {
            accountLocalDataSource.getAccountWithId(uid)
                ?.toAccount() ?: Account()
        }
    }

    override suspend fun getCurrentAccount(): Account {
        return withContext(ioDispatcher) {
            val id = sessionManager.requireUserId()
            accountLocalDataSource.getAccountWithId(uid = id)?.toAccount() ?: Account()

        }
    }

    override suspend fun searchAccountsByEmail(email: String): NetworkResult<List<Account>> {
        return executeWithTimeout {
            val accountDTOs = accountNetworkDataSource.fetchAccountsByEmail(email)
            val entities = accountDTOs.map { accountDTO -> accountDTO.toAccountEntity() }
            accountLocalDataSource.upsertAccounts(entities)
            accountDTOs.map { accountDTOs -> accountDTOs.toAccount() }
        }
    }


    override suspend fun updateBlockedUsers(otherUser: String): NetworkResult<Unit> {
        return executeWithTimeout {
            val ownerId = sessionManager.requireUserId()
            val owner = accountLocalDataSource.getAccountWithId(ownerId)

            owner?.let {
                val newList = owner.blockedUserList + otherUser
                accountLocalDataSource.updateBlockedList(list = newList, owner = ownerId)
                accountNetworkDataSource.updateBlockedList(list = newList, owner = ownerId)
            }
        }
    }

    override suspend fun removeUserFromBlockedList(uid: String): NetworkResult<Unit> {
        return executeWithTimeout {
            val ownerId = sessionManager.requireUserId()
            val owner = accountLocalDataSource.getAccountWithId(ownerId)

            owner?.let {
                val newList = owner.blockedUserList - uid
                accountLocalDataSource.updateBlockedList(list = newList, owner = ownerId)
                accountNetworkDataSource.updateBlockedList(list = newList, owner = ownerId)
            }
        }
    }

    override fun observeWhoIsBlock(
        block: String,
        isBlocked: String
    ): Flow<Boolean> {
        return accountLocalDataSource.observeBlockStatus(block)
            .map { list ->
                Timber.Forest.d("$list")
                list.contains(isBlocked)
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
            Timber.Forest.e(e)
            false
        }
    }

    override suspend fun updateHistory(ownerId: String, list: List<SearchHistoryResult>) {
        withContext(ioDispatcher) {
            val list = list.map { result ->
                SearchHistoryEntity(
                    ownerId = ownerId,
                    searchResultId = result.uid,
                )
            }
            accountLocalDataSource.updateSearchHistory(list)
        }
    }

    override fun getSearchHistory(owner: String): Flow<List<SearchHistoryResult>> {
        return accountLocalDataSource.observeSearchHistory(owner)
    }

    override suspend fun updateUserImage(uid: String, remoteUrl: String): NetworkResult<String> {
        return execute(dispatcher = ioDispatcher) {
            accountLocalDataSource.updateImageUrl(uid = uid, imgUrl = remoteUrl)
            accountNetworkDataSource.updateImageUrl(uid = uid, imgUrl = remoteUrl)
            uid
        }
    }
}