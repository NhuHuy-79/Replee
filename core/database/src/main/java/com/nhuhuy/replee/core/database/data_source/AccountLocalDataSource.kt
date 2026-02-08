package com.nhuhuy.replee.core.database.data_source

import android.util.Log
import com.nhuhuy.replee.core.database.entity.account.AccountDao
import com.nhuhuy.replee.core.database.entity.account.AccountEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountLocalDataSource @Inject constructor(
    private val dao: AccountDao
) {
    suspend fun upsertAccount(accountEntity: AccountEntity){
        dao.upsert(accountEntity)
    }

    suspend fun updateImageUrl(uid: String, imgUrl: String) {
        dao.updateImageUrl(uid, imgUrl)
    }

    suspend fun getAccountWithId(uid: String) : AccountEntity {
        return dao.getAccountWithUid(uid)
    }

    suspend fun upsertAccounts(list: List<AccountEntity>){
        dao.upsertAll(list)
    }

    suspend fun updateLogoutStatus(uid: String) {
        dao.updateLogoutStatus(uid)
    }

    fun observeBlockStatus(owner: String): Flow<List<String>> {
        return dao.observeBlockStatus(owner).map { accountEntity ->
            Log.d("AccountLocalSource", "observeBlockStatus: $accountEntity")
            accountEntity?.blockedUserList ?: emptyList()
        }
    }

    suspend fun updateBlockedList(owner: String, list: List<String>) {
        dao.updateBlockedList(owner, list)
    }

    suspend fun deleteAccount(accountEntity: AccountEntity) {
        dao.delete(accountEntity)
    }
}