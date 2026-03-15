package com.nhuhuy.replee.feature_chat.domain.usecase.account

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.AccountRepository
import javax.inject.Inject

class FetchAccountListUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(uids: List<String>): NetworkResult<Unit> {
        return accountRepository.fetchAccountList(uids = uids)
    }
}