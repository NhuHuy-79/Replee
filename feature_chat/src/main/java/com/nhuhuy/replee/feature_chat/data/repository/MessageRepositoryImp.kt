package com.nhuhuy.replee.feature_chat.data.repository

import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.mapResource
import com.nhuhuy.replee.core.common.error_handling.safeCall
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.feature_chat.data.mapper.MessageMapper
import com.nhuhuy.replee.feature_chat.data.source.chat.MessageNetworkDataSource
import com.nhuhuy.replee.feature_chat.data.source.conversation.ConversationNetworkDataSource
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.repository.MessageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class MessageRepositoryImp @Inject constructor(
    private val mapper: MessageMapper,
    private val messageNetworkDataSource: MessageNetworkDataSource,
    private val conversationDataSource: ConversationNetworkDataSource,
    private val dispatcher: CoroutineDispatcher,
) : MessageRepository {
    override fun observeMessageList(conversationId: String): Flow<Resource<List<Message>, RemoteFailure>> {
        return messageNetworkDataSource.observeMessageList(conversationId).mapResource { messageList ->
            messageList.map(mapper::fromRemoteToDomain)
        }.flowOn(dispatcher)
    }

    override suspend fun addNewMessage(
        message: Message,
        conversationId: String
    ) : Resource<Message, RemoteFailure> {
        return withContext(dispatcher){
            safeCall(
                throwable = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                            },
            ){
                val messageDTO = mapper.fromDomainToRemote(message)
                messageNetworkDataSource.addNewMessage(messageDTO, conversationId)
                val conversationDTO = conversationDataSource.getConversationById(conversationId)
                conversationDataSource.updateLastMessage(messageDTO, conversationDTO)
                message
            }
        }
    }

    override suspend fun markMessageAsRead(
        messageIds: List<String>,
        conversationId: String,
        receiverId: String
    ): Resource<Unit, RemoteFailure> {
        return  withContext(dispatcher){
            safeCall(
                throwable = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                            },
            ){
                val conversationDTO = conversationDataSource.getConversationById(conversationId)
                val receiverField = if (conversationDTO.user1.uid == receiverId) "user1" else "user2"
                val count = messageNetworkDataSource.markMessagesRead(conversationId = conversationId, messageIds = messageIds, receiverId = receiverId)
                conversationDataSource.updateUnReadMessageCount(conversationId = conversationId, receiverField = receiverField, count = count)
            }
        }
    }
}