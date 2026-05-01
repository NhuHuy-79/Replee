package com.nhuhuy.replee.core.domain.repository

import com.nhuhuy.replee.core.model.account.Account
import com.nhuhuy.replee.core.model.chat.SearchHistoryResult
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import kotlinx.coroutines.flow.Flow

interface AccountQueryRepository {
    suspend fun getAccountById(uid: String): Account
    suspend fun getCurrentAccount(): Account
    suspend fun searchAccountsByEmail(email: String): NetworkResult<List<Account>>
    fun observeWhoIsBlock(block: String, isBlocked: String): Flow<Boolean>
    suspend fun isBlocked(owner: String, otherUser: String): Boolean
    fun getSearchHistory(owner: String): Flow<List<SearchHistoryResult>>
}
