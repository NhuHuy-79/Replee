package com.nhuhuy.replee.feature_chat.domain.usecase.block

import com.nhuhuy.core.domain.repository.AccountRepository
import javax.inject.Inject

class CheckBlockUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(ownerId: String, otherUserId: String): Boolean {
        return accountRepository.isBlocked(ownerId, otherUserId)
    }
}