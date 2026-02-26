package com.nhuhuy.replee.feature_chat.data

import com.google.firebase.messaging.FirebaseMessaging
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.core.domain.repository.NetworkResultCaller
import com.nhuhuy.core.domain.utils.Logger
import com.nhuhuy.replee.core.common.mapper.toAccount
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.network.KtorService
import com.nhuhuy.replee.core.network.network.model.ConversationNotificationRequest
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
    private val accountLocalDataSource: AccountLocalDataSource,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val service: KtorService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NotifyService, NetworkResultCaller(dispatcher, logger) {
    override suspend fun getDeviceToken(): String {
        return messaging.token.await()
    }

    override suspend fun sendNotification(message: Message): NetworkResult<Unit> {
        return safeCall {
            var sender = accountLocalDataSource
                .getAccountWithId(message.senderId)
                ?.toAccount()

            if (sender == null) {
                sender = accountNetworkDataSource
                    .fetchAccountById(message.senderId)
                    .toAccount()
            }

            val conversationNotificationRequest = ConversationNotificationRequest(
                senderId = message.senderId,
                senderName = sender.name,
                receiverId = message.receiverId,
                content = message.content,
                imgUrl = sender.imageUrl,
                conversationId = message.conversationId
            )
            service.sendConversationMessage(sender.currentToken, conversationNotificationRequest)
        }
    }

}