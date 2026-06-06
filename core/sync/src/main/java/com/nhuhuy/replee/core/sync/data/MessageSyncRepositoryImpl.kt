package com.nhuhuy.replee.core.sync.data

import com.nhuhuy.replee.core.common.utils.IoDispatcher
import com.nhuhuy.replee.core.data.utils.executeWithTimeout
import com.nhuhuy.replee.core.domain.SessionManager
import com.nhuhuy.replee.core.model.chat.Message
import com.nhuhuy.replee.core.model.error_handling.NetworkResult
import com.nhuhuy.replee.core.network.data_source.transaction.NetworkMultipleWriteRunner
import com.nhuhuy.replee.core.network.mapper.toMessageDTO
import com.nhuhuy.replee.core.sync.domain.MessageSyncRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class MessageSyncRepositoryImpl @Inject constructor(
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val sessionManager: SessionManager,
    private val multipleWriteRunner: NetworkMultipleWriteRunner
) : MessageSyncRepository {
    override suspend fun deleteMessages(messages: List<Message>): NetworkResult<Unit> {
        return executeWithTimeout(dispatcher = ioDispatcher) {
            multipleWriteRunner.deleteMessagesAndUpdateConversations(
                messageDTOs = messages.map { message ->
                    message.toMessageDTO()
                }
            )
        }
    }

    override suspend fun pinMessages(
        messages: List<Message>,
        pinned: Boolean
    ): NetworkResult<Unit> {
        return executeWithTimeout(dispatcher = ioDispatcher) {
            multipleWriteRunner.pinMessagesAndUpdateConversation(
                messageDTOs = messages.map { message -> message.toMessageDTO() },
                pinned = pinned
            )
        }
    }

    override suspend fun sendMessages(messages: List<Message>): NetworkResult<Unit> {
        return executeWithTimeout(dispatcher = ioDispatcher) {
            multipleWriteRunner.sendMessagesAndUpdateConversation(
                messageDTOs = messages.map { message -> message.toMessageDTO() }
            )
        }
    }

    override suspend fun reactToMessages(messages: List<Message>): NetworkResult<Unit> {
        return executeWithTimeout(dispatcher = ioDispatcher) {
            multipleWriteRunner.reactToMessageAndUpdateConversation(
                messageDTOs = messages.map { message -> message.toMessageDTO() },
                currentUserId = sessionManager.getUserIdOrNull().orEmpty()
            )
        }
    }
}