package com.nhuhuy.replee.feature_chat.data

import com.google.firebase.messaging.FirebaseMessaging
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.safeCall
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.core.firebase.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.firebase.network.KtorService
import com.nhuhuy.replee.core.firebase.network.model.ConversationMessage
import com.nhuhuy.replee.core.firebase.network.model.NetworkMessage
import com.nhuhuy.replee.feature_chat.domain.model.Message
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

interface SendMessageService{
    suspend fun provideNewestToken() : String
    suspend fun sendMessage(message: Message) : Resource<Unit, RemoteFailure>
}

class SendMessageServiceImp @Inject constructor(
    private val messaging: FirebaseMessaging,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val service: KtorService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : SendMessageService {
    override suspend fun provideNewestToken(): String {
        return messaging.token.await()
    }

    override suspend fun sendMessage(message: Message) : Resource<Unit, RemoteFailure> {
        return withContext(dispatcher){
            safeCall(
                throwable = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                }
            ){
                val otherUser = accountNetworkDataSource.getAccountById(message.senderId)
                val conversationMessage = ConversationMessage(
                    senderId = message.senderId,
                    senderName = otherUser.name,
                    receiverId = message.receiverId,
                    content = message.content,
                    conversationId = message.conversationId
                )
                service.sendMessage(otherUser.currentToken, conversationMessage)
            }
        }
    }

}