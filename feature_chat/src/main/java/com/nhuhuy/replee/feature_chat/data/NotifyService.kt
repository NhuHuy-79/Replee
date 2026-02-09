package com.nhuhuy.replee.feature_chat.data

import com.google.firebase.messaging.FirebaseMessaging
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.NetworkResultCaller
import com.nhuhuy.core.domain.utils.Logger
import com.nhuhuy.replee.core.firebase.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.firebase.network.KtorService
import com.nhuhuy.replee.core.firebase.network.model.ConversationMessage
import com.nhuhuy.replee.feature_chat.domain.model.Message
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface NotifyService{
    suspend fun getDeviceToken() : String
    suspend fun sendNotification(message: Message): NetworkResult<Unit>
}

class NotifyServiceImp @Inject constructor(
    private val logger: Logger,
    private val messaging: FirebaseMessaging,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val service: KtorService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NotifyService, NetworkResultCaller(dispatcher, logger) {
    override suspend fun getDeviceToken(): String {
        return messaging.token.await()
    }

    override suspend fun sendNotification(message: Message): NetworkResult<Unit> {
        return safeCall {
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