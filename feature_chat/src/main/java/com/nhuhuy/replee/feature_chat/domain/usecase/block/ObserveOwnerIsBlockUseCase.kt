package com.nhuhuy.replee.feature_chat.domain.usecase.block

import com.nhuhuy.core.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveOwnerIsBlockUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(ownerId: String, otherUserId: String): Flow<Boolean> {
        return accountRepository.observeWhoIsBlock(
            block = ownerId,
            isBlocked = otherUserId
        )
    }
}