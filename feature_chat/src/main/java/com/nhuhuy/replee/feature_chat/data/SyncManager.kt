package com.nhuhuy.replee.feature_chat.data

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.NetworkResultCaller
import com.nhuhuy.core.domain.utils.Logger
import com.nhuhuy.replee.feature_chat.data.mapper.toConversationDTO
import com.nhuhuy.replee.feature_chat.data.mapper.toConversationPatch
import com.nhuhuy.replee.feature_chat.data.mapper.toMessage
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageDTO
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageNetworkDataSource
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface SyncManager {
    suspend fun updateMessageStatus(messageId: String, status: MessageStatus)
    suspend fun updateConversationStatus(conversationId: String, synced: Boolean)
    suspend fun syncMessage(): NetworkResult<Unit>
    suspend fun syncConversation(): NetworkResult<Unit>
    suspend fun cleanUpDatabase()
}

private const val CLEAN_UP_LIMIT: Int = 250

class SyncManagerImp @Inject constructor(
    private val logger: Logger,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val messageLocalDataSource: MessageLocalDataSource,
    private val messageNetworkDataSource: MessageNetworkDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource,
    private val conversationNetworkDataSource: ConversationNetworkDataSource
) : SyncManager, NetworkResultCaller(dispatcher, logger) {
    override suspend fun updateMessageStatus(
        messageId: String,
        status: MessageStatus
    ) {
       return withContext(dispatcher){
            messageLocalDataSource.updateSyncStatus(
                listOf(messageId), status)
        }
    }

    override suspend fun updateConversationStatus(
        conversationId: String,
        synced: Boolean
    ) {
        return withContext(dispatcher) {
            conversationLocalDataSource.updateConversationSyncedStatus(conversationId, synced)
        }
    }

    override suspend fun syncMessage(): NetworkResult<Unit> {
        return safeCall {
            val unSyncedMessages = messageLocalDataSource.getUnsyncedMessages().map { entity ->
                entity.toMessage().toMessageDTO()
            }
            val messageIds = unSyncedMessages.map { messageDTO -> messageDTO.messageId }

            val conversationIds = messageNetworkDataSource.sendMessages(unSyncedMessages)

            //Update all message'sync status
            messageLocalDataSource.updateSyncStatus(messageIds, MessageStatus.SYNCED)

            //Update all conversation's sync state
            conversationLocalDataSource.updateLastSyncedTime(
                conversationIds,
                System.currentTimeMillis()
            )
        }
    }

    override suspend fun syncConversation(): NetworkResult<Unit> {
        return safeCall {
            val conversationAndUsers = conversationLocalDataSource.getUnSyncedConversations()

            val conversationIds = conversationAndUsers.map { conversationAndUsers ->
                conversationAndUsers.conversation.id
            }
            val conversationPatchList = conversationAndUsers.map { conversationAndUser ->
                conversationAndUser.toConversationPatch()
            }
            //Upload ConversationDTO
            val conversationDTOList = conversationAndUsers.map { conversationAndUser ->
                conversationAndUser.toConversationDTO()
            }
            conversationNetworkDataSource.sendConversations(conversationDTOList)

            //Update Field In Conversation
            conversationNetworkDataSource.updateConversations(conversationPatchList)
            conversationLocalDataSource.updateSyncStatusOfConversations(conversationIds, true)
        }
    }

    override suspend fun cleanUpDatabase() {
        return withContext(dispatcher){
            messageLocalDataSource.deleteMessageByConversationId(CLEAN_UP_LIMIT)
        }
    }
}