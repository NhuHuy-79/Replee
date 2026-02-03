package com.nhuhuy.replee.feature_chat.domain.usecase.conversation_setting

import com.nhuhuy.replee.core.common.data.repository.AccountRepository
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import javax.inject.Inject

class BlockUserUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(
        otherUserId: String,
    ): Resource<Unit, RemoteFailure> {
        return accountRepository.updateBlockedUsers(otherUser = otherUserId)
    }
}