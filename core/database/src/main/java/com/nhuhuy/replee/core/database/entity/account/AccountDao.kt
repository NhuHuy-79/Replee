package com.nhuhuy.replee.core.database.entity.account

import androidx.room.Dao
import androidx.room.Query
import com.nhuhuy.replee.core.database.base.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao : BaseDao<AccountEntity>{
    @Query("SELECT * FROM accounts WHERE uid = :uid")
    suspend fun getAccountWithUid(uid: String) : AccountEntity

    @Query("UPDATE accounts SET logOut = true WHERE uid = :uid")
    suspend fun updateLogoutStatus(uid: String)

    @Query("UPDATE accounts SET blockedUserList = :list WHERE uid = :uid")
    suspend fun updateBlockedList(uid: String, list: List<String>)

    @Query("UPDATE accounts SET imageUrl = :imgUrl WHERE uid = :uid ")
    suspend fun updateImageUrl(uid: String, imgUrl: String)

    @Query("SELECT * FROM accounts WHERE uid = :uid")
    fun observeBlockStatus(uid: String): Flow<AccountEntity?>
}