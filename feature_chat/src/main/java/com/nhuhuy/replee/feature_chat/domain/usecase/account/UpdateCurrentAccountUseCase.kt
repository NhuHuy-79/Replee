package com.nhuhuy.replee.feature_chat.domain.usecase.account

import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.domain.repository.AccountRepository
import javax.inject.Inject

class UpdateCurrentAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(uid: String): NetworkResult<String> {
        return accountRepository.fetchAccount(uid)
    }
}
