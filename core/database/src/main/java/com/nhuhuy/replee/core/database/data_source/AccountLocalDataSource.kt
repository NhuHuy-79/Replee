package com.nhuhuy.replee.core.database.data_source

import android.util.Log
import com.nhuhuy.core.domain.model.SearchHistoryResult
import com.nhuhuy.replee.core.database.CoreDatabase
import com.nhuhuy.replee.core.database.entity.account.AccountEntity
import com.nhuhuy.replee.core.database.entity.search_history.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface AccountLocalDataSource {
    suspend fun upsertAccount(accountEntity: AccountEntity)
    suspend fun updateImageUrl(uid: String, imgUrl: String)
    suspend fun getAccountWithId(uid: String): AccountEntity?
    suspend fun upsertAccounts(list: List<AccountEntity>)
    suspend fun updateLogoutStatus(uid: String)
    suspend fun upsertSearchHistory(list: List<SearchHistoryEntity>)
    fun observeSearchHistory(uid: String): Flow<List<SearchHistoryResult>>
    suspend fun updateSearchHistory(list: List<SearchHistoryEntity>)
    fun observeBlockStatus(owner: String): Flow<List<String>>
    suspend fun updateBlockedList(owner: String, list: List<String>)
    suspend fun deleteAccount(accountEntity: AccountEntity)
}

class AccountLocalDataSourceImp @Inject constructor(
    private val coreDatabase: CoreDatabase,
) : AccountLocalDataSource {
    private val accountDao = coreDatabase.provideAccountDao()
    private val searchHistoryDao = coreDatabase.provideSearchHistoryDao()

    override suspend fun upsertAccount(accountEntity: AccountEntity) {
        accountDao.upsert(accountEntity)
    }

    override suspend fun updateImageUrl(uid: String, imgUrl: String) {
        accountDao.updateImageUrl(uid, imgUrl)
    }

    override suspend fun getAccountWithId(uid: String): AccountEntity? {
        return accountDao.getAccountWithUid(uid)
    }

    override suspend fun upsertAccounts(list: List<AccountEntity>) {
        accountDao.upsertAll(list)
    }

    override suspend fun updateLogoutStatus(uid: String) {
        accountDao.updateLogoutStatus(uid)
    }

    override suspend fun upsertSearchHistory(list: List<SearchHistoryEntity>) {
        searchHistoryDao.upsertAll(list)
    }

    override fun observeSearchHistory(uid: String): Flow<List<SearchHistoryResult>> {
        return searchHistoryDao.getSearchHistory(uid)
    }

    override suspend fun updateSearchHistory(list: List<SearchHistoryEntity>) {
        searchHistoryDao.upsertAll(list)
    }


    override fun observeBlockStatus(owner: String): Flow<List<String>> {
        return accountDao.observeBlockStatus(owner).map { accountEntity ->
            Log.d("AccountLocalSource", "observeBlockStatus: $accountEntity")
            accountEntity?.blockedUserList ?: emptyList()
        }
    }

    override suspend fun updateBlockedList(owner: String, list: List<String>) {
        accountDao.updateBlockedList(owner, list)
    }

    override suspend fun deleteAccount(accountEntity: AccountEntity) {
        accountDao.delete(accountEntity)
    }
}
