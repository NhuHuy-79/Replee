package com.nhuhuy.replee.feature_chat.data

import androidx.core.net.toUri
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.utils.ioExecute
import com.nhuhuy.replee.core.network.data_source.UploadFileService
import com.nhuhuy.replee.feature_chat.data.mapper.toConversationDTO
import com.nhuhuy.replee.feature_chat.data.mapper.toConversationPatch
import com.nhuhuy.replee.feature_chat.data.mapper.toMessage
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageDTO
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.message.MessageNetworkDataSource
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import com.nhuhuy.replee.feature_chat.domain.model.MessageType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

interface SyncManager {
    suspend fun updateMessageStatus(messageId: String, status: MessageStatus)
    suspend fun updateConversationStatus(conversationId: String, synced: Boolean)
    suspend fun syncMessage(): NetworkResult<Unit>
    suspend fun syncConversation(): NetworkResult<Unit>
    suspend fun syncImageMessage(): NetworkResult<Unit>
    suspend fun cleanUpDatabase()
}

private const val CLEAN_UP_LIMIT: Int = 250

class SyncManagerImp @Inject constructor(
    private val uploadFileService: UploadFileService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val messageLocalDataSource: MessageLocalDataSource,
    private val messageNetworkDataSource: MessageNetworkDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource,
    private val conversationNetworkDataSource: ConversationNetworkDataSource
) : SyncManager {
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
        return ioExecute {
            val unSyncedMessages = messageLocalDataSource.getUnsyncedMessages().map { entity ->
                entity.toMessage().toMessageDTO()
            }

            if (unSyncedMessages.isEmpty()) {
                Timber.e("No message to sync")
                return@ioExecute
            }

            val messageIds = unSyncedMessages.map { messageDTO -> messageDTO.messageId }

            //Send message to network
            val conversationIds = messageNetworkDataSource.sendMessages(unSyncedMessages)

            if (conversationIds.isEmpty()) {
                Timber.e("Failed to sync all message to network!")
                return@ioExecute
            }

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
        return ioExecute {
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
            conversationLocalDataSource.updateLastSyncedTime(
                conversationIds,
                System.currentTimeMillis()
            )
        }
    }

    override suspend fun syncImageMessage(): NetworkResult<Unit> {
        return ioExecute {
            val messageWithUri: Map<String, String> = messageLocalDataSource
                .getUnsyncedMessageByType(MessageType.IMAGE)
                .mapNotNull { message ->
                    message.localUriPath?.let { uri ->
                        message.messageId to uri
                    }
                }
                .toMap()

            if (messageWithUri.isEmpty()) {
                Timber.e("No image message to sync")
                return@ioExecute
            }

            val messageIdsWithURl: Map<String, String> =
                uploadFileService.uploadMessageWithUri(messageWithUri)

            if (messageIdsWithURl.isNotEmpty()) {
                Timber.e("Synced ${messageIdsWithURl.size} image message to network!")
                messageLocalDataSource.updateRemoteUrlMessage(messageIdsWithURl)
            } else {
                Timber.e("Failed to sync all image message to network!")
            }
        }
    }

    override suspend fun cleanUpDatabase() {
        return withContext(dispatcher){
            messageLocalDataSource.deleteMessageByConversationId(CLEAN_UP_LIMIT)
        }
    }
}