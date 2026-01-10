package com.nhuhuy.replee.core.database.data_source


import com.nhuhuy.replee.core.database.entity.account.AccountDao
import com.nhuhuy.replee.core.database.entity.account.AccountEntity
import javax.inject.Inject

class AccountLocalDataSource @Inject constructor(
    private val dao: AccountDao
) {
    suspend fun saveAccount(
        accountEntity: AccountEntity
    ){
        dao.upsert(accountEntity)
    }

    suspend fun getAccountWithId(uid: String) : AccountEntity {
        return dao.getAccountWithUid(uid)
    }

    suspend fun saveAccountList(list: List<AccountEntity>){
        dao.upsertAll(list)
    }

    suspend fun setLogOut(uid: String) {
        dao.setLogOut(uid)
    }

    suspend fun deleteAccount(accountEntity: AccountEntity) {
        dao.delete(accountEntity)
    }
}