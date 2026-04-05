package com.nhuhuy.replee.feature_chat.domain.usecase.sync

import com.nhuhuy.core.domain.repository.AccountRepository
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
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