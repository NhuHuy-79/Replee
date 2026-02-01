package com.nhuhuy.replee.core.common.data.repository

import com.nhuhuy.replee.core.common.base.BaseRepository
import com.nhuhuy.replee.core.common.data.model.Account
import com.nhuhuy.replee.core.common.data.model.toAccountDTO
import com.nhuhuy.replee.core.common.data.model.toAccountEntity
import com.nhuhuy.replee.core.common.error_handling.NetworkResult
import com.nhuhuy.replee.core.common.utils.Logger
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.firebase.data_source.AccountNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

interface IAccountRepository {
    suspend fun createAccount(account: Account): NetworkResult<Account>
}

class IAccountRepositoryImp @Inject constructor(
    private val logger: Logger,
    private val dispatcher: CoroutineDispatcher,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val accountLocalDataSource: AccountLocalDataSource,
) : IAccountRepository,
    BaseRepository(dispatcher, logger) {
    override suspend fun createAccount(account: Account): NetworkResult<Account> = safeCall {
        val entity = account.toAccountEntity()
        val dto = account.toAccountDTO()

        accountLocalDataSource.upsertAccount(entity)

        accountNetworkDataSource.sendAccount(dto)

        account
    }
}