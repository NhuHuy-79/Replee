package com.nhuhuy.replee.feature_chat.domain.usecase.block

import com.nhuhuy.core.domain.repository.AccountRepository
import javax.inject.Inject

class UnblockUserUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(otherUserId: String) {
        accountRepository.removeUserFromBlockedList(uid = otherUserId)
    }
}