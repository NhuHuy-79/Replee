package com.nhuhuy.replee.core.database.entity.account

import androidx.room.Dao
import androidx.room.Query
import com.nhuhuy.replee.core.database.base.BaseDao

@Dao
interface AccountDao : BaseDao<AccountEntity>{
    @Query("SELECT * FROM accounts WHERE uid = :uid")
    suspend fun getAccountWithUid(uid: String) : AccountEntity

    @Query("UPDATE accounts SET logOut = true WHERE uid = :uid")
    suspend fun updateLogoutStatus(uid: String)

    @Query("UPDATE accounts SET blockedUserList = :list WHERE uid = :uid")
    suspend fun updateBlockedList(uid: String, list: List<String>)
}