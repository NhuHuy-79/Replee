package com.nhuhuy.replee.core.sync.domain.usecase.conversation

import com.nhuhuy.replee.core.domain.repository.AccountRepository
import com.nhuhuy.replee.core.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SyncConversationUsersUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val accountRepository: AccountRepository
) {
    operator fun invoke(currentUserId: String): Flow<Unit> {
        return conversationRepository.observeOtherUserInConversation(currentUserId)
            .map { uids ->
                accountRepository.fetchAccountList(uids)
                Unit
            }
    }
}
