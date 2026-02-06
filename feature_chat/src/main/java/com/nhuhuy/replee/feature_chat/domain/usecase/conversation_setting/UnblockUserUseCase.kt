package com.nhuhuy.replee.feature_chat.domain.usecase.conversation_setting

import javax.inject.Inject

class UnblockUserUseCase @Inject constructor(
    private val accountRepository: com.nhuhuy.core.domain.repository.AccountRepository
) {
    suspend operator fun invoke(otherUserId: String) {
        accountRepository.removeUserFromBlockedList(uid = otherUserId)
    }
}