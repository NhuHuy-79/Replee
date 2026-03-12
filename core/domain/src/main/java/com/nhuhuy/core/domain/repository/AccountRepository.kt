package com.nhuhuy.core.domain.repository

import com.nhuhuy.core.domain.model.Account
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.model.SearchHistoryResult
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun createAccount(token: String, account: Account): NetworkResult<Account>
    suspend fun createAccount(account: Account): NetworkResult<Account>
    suspend fun updateDeviceToken(token: String): NetworkResult<Unit>
    suspend fun getAccountById(uid: String): Account
    suspend fun getCurrentAccount(): Account
    suspend fun searchAccountsByEmail(email: String): NetworkResult<List<Account>>
    suspend fun updateBlockedUsers(otherUser: String): NetworkResult<Unit>
    suspend fun removeUserFromBlockedList(uid: String): NetworkResult<Unit>
    fun observeBlockStatus(owner: String, otherUser: String): Flow<Boolean>
    suspend fun isBlocked(owner: String, otherUser: String): Boolean
    suspend fun updateHistory(ownerId: String, list: List<SearchHistoryResult>)
    fun getSearchHistory(owner: String): Flow<List<SearchHistoryResult>>
}
