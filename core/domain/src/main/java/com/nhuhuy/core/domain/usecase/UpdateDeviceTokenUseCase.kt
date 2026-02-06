package com.nhuhuy.core.domain.usecase

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.AccountRepository

class UpdateDeviceTokenUseCase(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(token: String): NetworkResult<Unit> {
        return accountRepository.updateDeviceToken(token)
    }
}