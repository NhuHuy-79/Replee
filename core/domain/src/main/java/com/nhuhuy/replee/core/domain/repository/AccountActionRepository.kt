package com.nhuhuy.replee.core.domain.repository

import com.nhuhuy.replee.core.model.account.Account
import com.nhuhuy.replee.core.model.chat.SearchHistoryResult
import com.nhuhuy.replee.core.model.error_handling.NetworkResult

interface AccountActionRepository {
    suspend fun createAccount(token: String, account: Account): NetworkResult<Account>
    suspend fun fetchAccount(uid: String): NetworkResult<String>
    suspend fun fetchAccountList(uids: List<String>): NetworkResult<Unit>
    suspend fun updateDeviceToken(token: String): NetworkResult<Unit>
    suspend fun updateBlockedUsers(otherUser: String): NetworkResult<Unit>
    suspend fun removeUserFromBlockedList(uid: String): NetworkResult<Unit>
    suspend fun updateHistory(ownerId: String, list: List<SearchHistoryResult>)
    suspend fun updateUserImage(uid: String, remoteUrl: String): NetworkResult<String>
}
