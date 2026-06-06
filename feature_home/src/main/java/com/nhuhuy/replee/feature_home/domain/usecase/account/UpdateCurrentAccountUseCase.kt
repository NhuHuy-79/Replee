package com.nhuhuy.replee.feature_home.domain.usecase.account

import com.nhuhuy.replee.core.domain.repository.AccountRepository
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import javax.inject.Inject

class UpdateCurrentAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(uid: String): NetworkResult<String> {
        return accountRepository.fetchAccount(uid)
    }
}
