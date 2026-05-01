package com.nhuhuy.replee.core.domain.usecase

import com.nhuhuy.replee.core.model.NetworkResult
import com.nhuhuy.replee.core.domain.repository.AccountRepository

class UpdateDeviceTokenUseCase(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(token: String): NetworkResult<Unit> {
        return accountRepository.updateDeviceToken(token)
    }
}
