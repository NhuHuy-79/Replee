package com.nhuhuy.replee.core.database.data_source

import android.util.Log
import com.nhuhuy.core.domain.model.SearchHistoryResult
import com.nhuhuy.replee.core.database.CoreDatabase
import com.nhuhuy.replee.core.database.entity.account.AccountEntity
import com.nhuhuy.replee.core.database.entity.search_history.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountLocalDataSource @Inject constructor(
    private val coreDatabase: CoreDatabase,
) {
    private val accountDao = coreDatabase.provideAccountDao()
    private val searchHistoryDao = coreDatabase.provideSearchHistoryDao()
    suspend fun upsertAccount(accountEntity: AccountEntity){
        accountDao.upsert(accountEntity)
    }

    suspend fun updateImageUrl(uid: String, imgUrl: String) {
        accountDao.updateImageUrl(uid, imgUrl)
    }

    suspend fun getAccountWithId(uid: String): AccountEntity? {
        return accountDao.getAccountWithUid(uid)
    }

    suspend fun upsertAccounts(list: List<AccountEntity>){
        accountDao.upsertAll(list)
    }

    suspend fun updateLogoutStatus(uid: String) {
        accountDao.updateLogoutStatus(uid)
    }

    suspend fun upsertSearchHistory(list: List<SearchHistoryEntity>) {
        searchHistoryDao.upsertAll(list)
    }

    fun observeSearchHistory(uid: String): Flow<List<SearchHistoryResult>> {
        return searchHistoryDao.getSearchHistory(uid)
    }

    suspend fun updateSearchHistory(list: List<SearchHistoryEntity>) {
        searchHistoryDao.upsertAll(list)
    }


    fun observeBlockStatus(owner: String): Flow<List<String>> {
        return accountDao.observeBlockStatus(owner).map { accountEntity ->
            Log.d("AccountLocalSource", "observeBlockStatus: $accountEntity")
            accountEntity?.blockedUserList ?: emptyList()
        }
    }

    suspend fun updateBlockedList(owner: String, list: List<String>) {
        accountDao.updateBlockedList(owner, list)
    }

    suspend fun deleteAccount(accountEntity: AccountEntity) {
        accountDao.delete(accountEntity)
    }
}