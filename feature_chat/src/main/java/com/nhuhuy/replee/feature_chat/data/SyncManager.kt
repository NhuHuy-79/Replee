package com.nhuhuy.replee.feature_chat.data

import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.safeCall
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.feature_chat.data.mapper.toMessage
import com.nhuhuy.replee.feature_chat.data.mapper.toMessageDTO
import com.nhuhuy.replee.feature_chat.data.source.chat.MessageLocalDataSource
import com.nhuhuy.replee.feature_chat.data.source.chat.MessageNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationLocalDataSource
import com.nhuhuy.replee.feature_chat.domain.model.MessageStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

interface SyncManager {
    suspend fun updateMessageStatus(messageId: String, status: MessageStatus)
    suspend fun syncMessage() : Resource<Unit, RemoteFailure>
    suspend fun cleanUpDatabase()
}

private const val CLEAN_UP_LIMIT : Int = 15

class SyncManagerImp @Inject constructor(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val messageLocalDataSource: MessageLocalDataSource,
    private val messageNetworkDataSource: MessageNetworkDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource
) : SyncManager{
    override suspend fun updateMessageStatus(
        messageId: String,
        status: MessageStatus
    ) {
       return withContext(dispatcher){
            messageLocalDataSource.updateSyncStatus(
                listOf(messageId), status)
        }
    }

    override suspend fun syncMessage() : Resource<Unit, RemoteFailure> {
        return withContext(dispatcher){
            safeCall(
                throwable = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                }
            ){
                val unSyncedMessages = messageLocalDataSource.getUnsyncedMessages().map { entity ->
                    entity.toMessage().toMessageDTO()
                }
                val messageIds = unSyncedMessages.map { messageDTO -> messageDTO.messageId }
                val conversationIds = messageNetworkDataSource.sendMessages(unSyncedMessages)
                messageLocalDataSource.updateSyncStatus(messageIds, MessageStatus.SYNCED)
                conversationLocalDataSource.updateLastSyncedTime(conversationIds, System.currentTimeMillis())
            }
        }
    }

    override suspend fun cleanUpDatabase() {
        return withContext(dispatcher){
            messageLocalDataSource.deleteMessageByConversationId(CLEAN_UP_LIMIT)
        }
    }
}