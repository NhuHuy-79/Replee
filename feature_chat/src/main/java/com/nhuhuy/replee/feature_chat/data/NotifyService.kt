package com.nhuhuy.replee.feature_chat.data

import com.google.firebase.messaging.FirebaseMessaging
import com.nhuhuy.replee.core.common.error_handling.RemoteFailure
import com.nhuhuy.replee.core.common.error_handling.Resource
import com.nhuhuy.replee.core.common.error_handling.safeCall
import com.nhuhuy.replee.core.common.toRemoteFailure
import com.nhuhuy.replee.core.firebase.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.firebase.network.KtorService
import com.nhuhuy.replee.core.firebase.network.model.ConversationMessage
import com.nhuhuy.replee.feature_chat.domain.model.Message
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

interface NotifyService{
    suspend fun getDeviceToken() : String
    suspend fun sendNotification(message: Message) : Resource<Unit, RemoteFailure>
}

class NotifyServiceImp @Inject constructor(
    private val messaging: FirebaseMessaging,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val service: KtorService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NotifyService {
    override suspend fun getDeviceToken(): String {
        return messaging.token.await()
    }

    override suspend fun sendNotification(message: Message) : Resource<Unit, RemoteFailure> {
        return withContext(dispatcher){
            safeCall(
                throwable = { e ->
                    Timber.e(e)
                    e.toRemoteFailure()
                }
            ){
                val otherUser = accountNetworkDataSource.fetchAccountById(message.senderId)
                val conversationMessage = ConversationMessage(
                    senderId = message.senderId,
                    senderName = otherUser.name,
                    receiverId = message.receiverId,
                    content = message.content,
                    conversationId = message.conversationId
                )
                service.sendConversationMessage(otherUser.currentToken, conversationMessage)
            }
        }
    }

}