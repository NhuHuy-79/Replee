package com.nhuhuy.replee.feature_chat.data

import com.google.firebase.messaging.FirebaseMessaging
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.mapper.toAccount
import com.nhuhuy.replee.core.common.utils.execute
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.network.api.KtorService
import com.nhuhuy.replee.core.network.api.fcm.ContentType
import com.nhuhuy.replee.core.network.api.fcm.ConversationNotificationRequest
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.FirebaseAuthEmailService
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.model.MessageType
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

interface NotifyService{
    suspend fun getDeviceToken() : String
    suspend fun sendNotification(message: Message): NetworkResult<Unit>
}

class NotifyServiceImp @Inject constructor(
    private val messaging: FirebaseMessaging,
    private val firebaseAuthEmailService: FirebaseAuthEmailService,
    private val accountLocalDataSource: AccountLocalDataSource,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val service: KtorService
) : NotifyService {
    override suspend fun getDeviceToken(): String {
        return messaging.token.await()
    }

    override suspend fun sendNotification(message: Message): NetworkResult<Unit> {
        return execute {
            val contentType = when (message.type) {
                MessageType.TEXT -> ContentType.PLAIN_TEXT
                MessageType.IMAGE -> ContentType.IMAGE_URL
            }
            val receiver = accountNetworkDataSource
                .fetchAccountById(message.receiverId)
                .toAccount()
            var sender = accountLocalDataSource
                .getAccountWithId(message.senderId)
                ?.toAccount()

            if (sender == null) {
                sender = accountNetworkDataSource
                    .fetchAccountById(message.senderId)
                    .toAccount()
            }
            val authenticatedTokenId = firebaseAuthEmailService.getCurrentUser()?.getIdToken(true)
                ?.await()?.token ?: throw Exception("User not authenticated")

            val conversationNotificationRequest = ConversationNotificationRequest(
                senderName = sender.name,
                receiverId = message.receiverId,
                content = message.content,
                imgUrl = sender.imageUrl,
                contentType = contentType,
                conversationId = message.conversationId
            )

            Timber.d("token: ${receiver.currentToken}")
            service.sendConversationMessage(
                authenticationId = authenticatedTokenId,
                deviceToken = receiver.currentToken,
                conversationNotificationRequest
            )
        }
    }

}