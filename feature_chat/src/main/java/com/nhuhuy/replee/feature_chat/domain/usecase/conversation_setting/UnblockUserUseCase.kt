package com.nhuhuy.replee.feature_chat.domain.usecase.conversation_setting

import com.nhuhuy.replee.core.common.data.repository.AccountRepository
import javax.inject.Inject

class UnblockUserUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(otherUserId: String) {
        accountRepository.removeUserFromBlockedList(uid = otherUserId)
    }
}