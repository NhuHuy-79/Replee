package com.nhuhuy.replee.feature_chat.domain.usecase.block

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.AccountRepository
import javax.inject.Inject

class BlockUserUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(
        otherUserId: String,
    ): NetworkResult<Unit> {
        return accountRepository.updateBlockedUsers(otherUser = otherUserId)
    }
}