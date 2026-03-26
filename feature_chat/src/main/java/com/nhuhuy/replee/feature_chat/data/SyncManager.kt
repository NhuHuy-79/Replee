package com.nhuhuy.replee.feature_chat.data

import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.data.utils.execute
import com.nhuhuy.replee.core.network.data_source.UploadFileService
import com.nhuhuy.replee.core.network.quailify.Retrofit
import com.nhuhuy.replee.feature_chat.data.mapper.toMessage
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageDTO
import com.nhuhuy.replee.feature_chat.data.mapper.toUpdatePatch
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
    suspend fun syncConversation(uid: String): NetworkResult<Unit>
    suspend fun uploadFile(): NetworkResult<Unit>
    suspend fun cleanUpDatabase()
}

private const val CLEAN_UP_LIMIT: Int = 250

class SyncManagerImp @Inject constructor(
    @Retrofit private val uploadFileService: UploadFileService,
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
        return withContext(dispatcher) {
            messageLocalDataSource.updateMessageStatus(messageId = messageId, status = status)
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
        return execute {
            val unSyncedMessages = messageLocalDataSource.getUnsyncedMessages()
                .filter { entity ->
                    entity.type == MessageType.TEXT.name ||
                            entity.remoteUrl != null
                }
                .map { entity ->
                entity.toMessage().toMessageDTO()
            }

            if (unSyncedMessages.isEmpty()) {
                Timber.d("No message to sync")
                return@execute
            }

            val messageIds = unSyncedMessages.map { messageDTO -> messageDTO.messageId }

            //Send message to network
            val conversationIds = messageNetworkDataSource.sendMessages(unSyncedMessages)

            //Update all MessagePack status
            messageLocalDataSource.updateSyncStatus(messageIds, MessageStatus.SYNCED)

            //Update all conversation's sync state
            conversationLocalDataSource.updateLastSyncedTime(
                conversationIds = conversationIds,
                lastSyncedTime = System.currentTimeMillis()
            )
        }
    }

    override suspend fun syncConversation(uid: String): NetworkResult<Unit> {
        return execute {
            val unSyncedConversations = conversationLocalDataSource.getUnSyncedConversations()
            val conversationIds: List<String> = unSyncedConversations.map { conversationAndUser ->
                conversationAndUser.conversation.id
            }
            val conversationUpdatePatches: List<Map<String, Any>> =
                unSyncedConversations.map { conversationAndUser ->
                    conversationAndUser.toUpdatePatch(uid)
                }
            if (conversationUpdatePatches.isEmpty()) {
                Timber.d("No conversation to sync")
                return@execute
            }

            //Update conversation in network
            conversationNetworkDataSource.updateConversationDataMap(conversationUpdatePatches)
            //Update synced conversation in local
            conversationLocalDataSource.updateSyncStatusOfConversations(conversationIds, true)
        }
    }

    override suspend fun uploadFile(): NetworkResult<Unit> {
        return execute {
            val messageWithUri: Map<String, String> = messageLocalDataSource
                .getUnsyncedMessageByType(MessageType.IMAGE)
                .mapNotNull { message ->
                    message.localUriPath?.let { uri ->
                        message.messageId to uri
                    }
                }
                .toMap()

            if (messageWithUri.isEmpty()) {
                return@execute
            }

            val messageIdsWithURl: Map<String, String> =
                uploadFileService.uploadMessageWithUri(messageWithUri)

            if (messageIdsWithURl.isNotEmpty()) {
                Timber.d("Synced ${messageIdsWithURl.size} image message to network!")
                messageLocalDataSource.updateRemoteUrlMessage(messageIdsWithURl)
                syncMessage()
            } else {
                Timber.e("Failed to sync all image message to network!")
            }
        }
    }

    override suspend fun cleanUpDatabase() {
        return withContext(dispatcher) {
            messageLocalDataSource.deleteMessageByConversationId(CLEAN_UP_LIMIT)
        }
    }
}