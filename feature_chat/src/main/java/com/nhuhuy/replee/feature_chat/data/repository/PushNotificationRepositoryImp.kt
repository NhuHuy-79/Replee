package com.nhuhuy.replee.feature_chat.data.repository

import com.nhuhuy.core.domain.SessionManager
import com.nhuhuy.core.domain.model.NetworkResult
import com.nhuhuy.replee.core.common.mapper.toAccount
import com.nhuhuy.replee.core.common.utils.execute
import com.nhuhuy.replee.core.database.data_source.AccountLocalDataSource
import com.nhuhuy.replee.core.network.api.fcm.ContentType
import com.nhuhuy.replee.core.network.api.fcm.ConversationNotificationRequest
import com.nhuhuy.replee.core.network.data_source.AccountNetworkDataSource
import com.nhuhuy.replee.core.network.data_source.PushNotificationNetworkDataSource
import com.nhuhuy.replee.feature_chat.domain.model.Message
import com.nhuhuy.replee.feature_chat.domain.model.MessageType
import com.nhuhuy.replee.feature_chat.domain.repository.PushNotificationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class PushNotificationRepositoryImp @Inject constructor(
    private val sessionManager: SessionManager,
    private val accountLocalDataSource: AccountLocalDataSource,
    private val accountNetworkDataSource: AccountNetworkDataSource,
    private val pushNotificationNetworkDataSource: PushNotificationNetworkDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PushNotificationRepository {

    private suspend fun createConversationRequest(
        message: Message
    ): Pair<String, ConversationNotificationRequest>? {
        return withContext(ioDispatcher) {
            try {
                val sender = accountLocalDataSource.getAccountWithId(message.senderId)?.toAccount()
                    ?: accountNetworkDataSource.fetchAccountById(message.senderId).toAccount()
                //Get new token
                val receiver =
                    accountNetworkDataSource.fetchAccountById(message.receiverId).toAccount()

                val token = receiver.currentToken
                val request = ConversationNotificationRequest(
                    senderName = sender.name,
                    receiverId = receiver.id,
                    content = message.content,
                    contentType = getContentTypeFromMessage(message),
                    imgUrl = sender.imageUrl,
                    conversationId = message.conversationId
                )

                token to request
            } catch (e: Exception) {
                Timber.e(e)
                null
            }
        }
    }

    override suspend fun getCurrentToken(): NetworkResult<String> = execute {
        pushNotificationNetworkDataSource.getDeviceToken()
    }

    override suspend fun pushNotification(
        message: Message
    ): NetworkResult<Unit> {
        return execute {
            val authenticationId = sessionManager.getAuthenticationToken()
                .first()
                .ifEmpty { throw Exception("User not authenticated") }

            val tokenWithRequest = createConversationRequest(message)

            if (tokenWithRequest == null) {
                throw Exception("No token to send notification")
            }

            val token = tokenWithRequest.first
            val request = tokenWithRequest.second


            pushNotificationNetworkDataSource.sendNotification(
                deviceToken = token,
                authenticationId = authenticationId,
                request = request
            )
        }
    }

    private fun getContentTypeFromMessage(message: Message): ContentType {
        return when (message.type) {
            MessageType.TEXT -> ContentType.PLAIN_TEXT
            MessageType.IMAGE -> ContentType.IMAGE_URL
        }
    }
}