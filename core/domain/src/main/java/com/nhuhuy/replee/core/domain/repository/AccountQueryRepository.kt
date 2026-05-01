package com.nhuhuy.replee.core.domain.repository

import com.nhuhuy.replee.core.model.Account
import com.nhuhuy.replee.core.model.NetworkResult
import com.nhuhuy.replee.core.model.SearchHistoryResult
import kotlinx.coroutines.flow.Flow

interface AccountQueryRepository {
    suspend fun getAccountById(uid: String): Account
    suspend fun getCurrentAccount(): Account
    suspend fun searchAccountsByEmail(email: String): NetworkResult<List<Account>>
    fun observeWhoIsBlock(block: String, isBlocked: String): Flow<Boolean>
    suspend fun isBlocked(owner: String, otherUser: String): Boolean
    fun getSearchHistory(owner: String): Flow<List<SearchHistoryResult>>
}
